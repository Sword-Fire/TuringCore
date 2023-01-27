package net.geekmc.turingcore.data.player

import kotlinx.coroutines.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import net.geekmc.turingcore.data.player.PlayerDataService.getPlayerData
import net.geekmc.turingcore.data.serialization.addMinestomSerializers
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.coroutine.MinestomSync
import net.minestom.server.entity.Player
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly
import java.nio.file.Path
import java.time.Duration
import java.util.*
import kotlin.io.path.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.createType
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private typealias PlayerDataMap = HashMap<String, PlayerData>

/**
 * 获取玩家的某一类型的数据。只允许在主线程中使用。
 * 警告：在你的代码段运行完毕、让出主线程控制权后，通过该方法获取的引用可能在任何时候失效。
 * 因此，当你在某一时刻获取了主线程控制权，且想要访问玩家数据，总是应该通过 [getPlayerData] 方法来获取新的引用，且不要存放这个引用。
 */
inline fun <reified T : PlayerData> Player.getData(): T {
    return getPlayerData(this)
}

/**
 * 玩家数据服务。
 */
object PlayerDataService : Service() {

    /**
     * 注册一个玩家数据类。
     * @param T 玩家数据类型
     */
    inline fun <reified T : PlayerData> register() {
        val clazz = T::class
        clazzToIdentifierMap[clazz] = clazz.qualifiedName!!
        val action: SerializersModuleBuilder.() -> Unit = {
            @Suppress("UNCHECKED_CAST")
            val serializer = serializer(clazz.createType()) as KSerializer<T>
            polymorphic(PlayerData::class, clazz, serializer)
        }
        clazzToAddSerializerFunctionMap[clazz] = action
        updateJson()
    }

    // TODO: 待测试

    /**
     * 在主线程中调用离线玩家的数据。文件读取会阻塞主线程，因此较慢。在传入的代码段中，使用 [OfflinePlayerDataContext.getData] 获取数据。
     */
    fun useOfflinePlayerData(uuid: UUID, action: OfflinePlayerDataContext.() -> Unit) {
        loadPlayerData(uuid)
        OfflinePlayerDataContext(uuid).action()
        savePlayerData(uuid)
    }
    class OfflinePlayerDataContext(val uuid: UUID) {
        inline fun <reified T : PlayerData> getData(): T {
            return getPlayerData(uuid)
        }
    }

    // 存储所有玩家数据的Map，只允许在主线程中访问。
    val uuidToDataMap = HashMap<UUID, PlayerDataMap>()
    val clazzToIdentifierMap = HashMap<KClass<out PlayerData>, String>()
    val clazzToAddSerializerFunctionMap = HashMap<KClass<out PlayerData>, (SerializersModuleBuilder.() -> Unit)>()
    // 保存玩家数据时在主线程使用
    private lateinit var mainThreadJson: Json

    // 加载玩家数据时在服务线程使用
    @Volatile
    private lateinit var serviceThreadJson: Json

    /**
     * 用于玩家数据读写的协程域。单线程以保证不会出现并发读写问题。
     * "从文件读取玩家数据并构建玩家的所有 PlayerData "，与"将玩家的所有 PlayerData 序列化并写入文件",这两个过程都应完全在此协程域进行。
     */
    @OptIn(DelicateCoroutinesApi::class)
    private val playerDataServiceContext = newSingleThreadContext("PlayerDataDispatcher")

    private val UUID.dataFile: Path
        @Suppress("spellCheckingInspection")
        get() = Path.of("playerdata/${this}.json")

    @OptIn(ExperimentalTime::class)
    override fun onEnable() {

        // 最高优先级。
        EventNodes.INTERNAL_HIGHEST.listenOnly<AsyncPlayerPreLoginEvent> {
            if (!loadPlayerData(player.uuid)) {
                player.kick("读取数据超时")
            }
        }

        // 最低优先级，待其他插件处理完毕后再保存数据。
        EventNodes.INTERNAL_LOWEST.listenOnly<PlayerDisconnectEvent> {
            savePlayerData(player.uuid)
        }

        // 定时保存，延时30分钟后进行第一次检查，并以后每30分钟检查一次
        val saveInterval = Duration.ofMinutes(30)
        Manager.scheduler.buildTask {
            // 切到主线程
            val time = measureTime {
                Manager.connection.onlinePlayers.forEach { savePlayerData(it.uuid) }
            }.inWholeMilliseconds
            logger.info("定时保存玩家数据，耗时 $time ms")
        }.delay(saveInterval).repeat(saveInterval).schedule()
    }

    inline fun <reified T : PlayerData> getPlayerData(p: Player): T {
        return getPlayerData(p.uuid)
    }

    inline fun <reified T : PlayerData> getPlayerData(uuid: UUID): T {
        val identifier = clazzToIdentifierMap[T::class] ?: error("Identifier of Class ${T::class.qualifiedName} not exists")
        val playerDataMap = uuidToDataMap[uuid] ?: error("Can not find the data map of player $uuid in uuidToDataMap")
        val data = playerDataMap[identifier] ?: error("Data with identifier $identifier not found in player $uuid")
        return data as? T ?: error("While getting data $identifier of player $uuid, failed to convert PlayerData to ${T::class.simpleName}")
    }

    /**
     * 加载玩家数据，会阻塞调用线程。可能在玩家进服时异步调用，可能在主线程需求离线玩家数据时同步调用。
     * @return 是否成功读取。
     */
    private fun loadPlayerData(uuid: UUID): Boolean {
        return runBlocking {
            withContext(playerDataServiceContext) {
                var isReadFileSuccessful = true
                val fileContent = if (uuid.dataFile.exists()) {
                    withTimeoutOrNull(5000L) { uuid.dataFile.readText() } ?: run {
                        isReadFileSuccessful = false
                        "__FAILED__"
                    }
                } else "{}"
                // 读取文件失败，直接返回。
                if (!isReadFileSuccessful) {
                    logger.warn("读取玩家 $uuid 的数据失败")
                    withContext(Dispatchers.MinestomSync) {
                        uuidToDataMap.remove(uuid)
                    }
                    return@withContext false
                }
                val dataMap: PlayerDataMap = serviceThreadJson.decodeFromString(fileContent)
                // 将文件中没有的数据设为默认值。
                for ((clazz, identifier) in clazzToIdentifierMap) {
                    dataMap[identifier] ?: apply {
                        dataMap[identifier] = clazz.createInstance()
                    }
                }
                withContext(Dispatchers.MinestomSync) {
                    uuidToDataMap[uuid] = dataMap
                }
                true
            }
        }
    }

    /**
     * 在主线程中将玩家数据序列化，随后将序列化后的数据异步写入文件。
     */
    private fun savePlayerData(uuid: UUID) {
        val content = mainThreadJson.encodeToString(uuidToDataMap[uuid])
        // 在玩家数据线程写入文件
        CoroutineScope(playerDataServiceContext).launch {
            val file = uuid.dataFile
            if (file.notExists()) {
                file.parent.createDirectories()
                file.createFile()
            }
            file.writeText(content)
        }
    }

    /**
     * 更新用于序列化和反序列化的 Json 。每次添加新的玩家数据类型后都要更新。
     */
    fun updateJson() {
        mainThreadJson = buildJson()
        serviceThreadJson = buildJson()
    }

    private fun buildJson() = Json {
        serializersModule = SerializersModule {
            addMinestomSerializers(this)
            for ((_, func) in clazzToAddSerializerFunctionMap) {
                func(this)
            }
        }
    }

}