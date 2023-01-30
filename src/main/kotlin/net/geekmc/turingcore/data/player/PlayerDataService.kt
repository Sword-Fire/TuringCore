package net.geekmc.turingcore.data.player

import kotlinx.coroutines.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import net.geekmc.turingcore.data.serialization.addMinestomSerializers
import net.geekmc.turingcore.di.TuringCoreDIAware
import net.geekmc.turingcore.di.turingCoreDi
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.framework.AutoRegister
import net.geekmc.turingcore.player.uuid.UUIDService
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.coroutine.MinestomSync
import net.minestom.server.entity.Player
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly
import java.nio.file.Path
import java.nio.file.StandardCopyOption
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
 * 玩家数据服务。
 */
@AutoRegister
object PlayerDataService : Service(), TuringCoreDIAware {

    /**
     * 注册一个玩家数据类。
     * @param T 玩家数据类型
     */
    fun <T : PlayerData> register(clazz: KClass<T>) {
        clazzToIdentifierMap[clazz] = clazz.qualifiedName!!
        val action: SerializersModuleBuilder.() -> Unit = {
            @Suppress("UNCHECKED_CAST")
            val serializer = serializer(clazz.createType()) as KSerializer<T>
            polymorphic(PlayerData::class, clazz, serializer)
        }
        clazzToAddSerializerActionMap[clazz] = action
        updateJson()
    }

    // 存储所有玩家数据的Map，只允许在主线程中访问。
    private val uuidToDataMap = HashMap<UUID, PlayerDataMap>()
    private val clazzToIdentifierMap = HashMap<KClass<out PlayerData>, String>()
    private val clazzToAddSerializerActionMap = HashMap<KClass<out PlayerData>, (SerializersModuleBuilder.() -> Unit)>()

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
    private val singleThreadContext = newSingleThreadContext("PlayerDataDispatcher")

    private val UUID.dataFile: Path
        @Suppress("spellCheckingInspection")
        get() = Path.of("playerdata/${this}.json")
    private val UUID.tempDataFile: Path
        @Suppress("spellCheckingInspection")
        get() = Path.of("playerdata/${this}.json.tmp")

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

    fun <T : PlayerData> getPlayerData(uuid: UUID, clazz: KClass<out PlayerData>): T {
        val identifier = clazzToIdentifierMap[clazz] ?: error("Identifier of Class ${clazz.qualifiedName} not exists")
        val playerDataMap = uuidToDataMap[uuid] ?: error("Can not find the data map of player $uuid in uuidToDataMap")
        val data = playerDataMap[identifier] ?: error("Data with identifier $identifier not found in player $uuid")
        require(data.available) { "Data with identifier $identifier is not available in player $uuid because the player had once disconnected from the server, though he may be online now" }
        @Suppress("UNCHECKED_CAST")
        return data as? T
            ?: error("While getting data $identifier of player $uuid, failed to convert PlayerData to ${clazz.simpleName}")
    }

    fun withOfflinePlayerData(uuid: UUID, action: OfflinePlayerDataContext.() -> Unit): Boolean {
        if (!loadPlayerData(uuid)) {
            return false
        }
        OfflinePlayerDataContext(uuid).action()
        savePlayerData(uuid)
        return true
    }

    fun withOfflinePlayerData(username: String, action: OfflinePlayerDataContext.() -> Unit): Boolean {
        return withOfflinePlayerData(UUIDService.getUUID(username), action)
    }

    /**
     * 加载玩家数据，会阻塞调用线程。可能在玩家进服时异步调用，可能在主线程需求离线玩家数据时同步调用。
     * @return 是否成功读取。
     */
    private fun loadPlayerData(uuid: UUID): Boolean {
        return runBlocking {
            withContext(singleThreadContext) {
                var isReadFileSuccessful = true
                val fileContent = if (uuid.dataFile.exists()) {
                    withTimeoutOrNull(5000L) { uuid.dataFile.readText() } ?: run {
                        isReadFileSuccessful = false
                        "__FAILED__"
                    }
                } else "{}"
                // 读取文件失败，直接返回。
                if (!isReadFileSuccessful) {
                    logger.error("读取玩家 $uuid 的数据失败")
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
        CoroutineScope(singleThreadContext).launch {
            val tempFile = uuid.tempDataFile
            if (tempFile.notExists()) {
                tempFile.parent.createDirectories()
                tempFile.createFile()
            }
            tempFile.writeText(content)
            tempFile.moveTo(uuid.dataFile, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    /**
     * 更新用于序列化和反序列化的 Json 。每次添加新的玩家数据类型后都要更新。
     */
    private fun updateJson() {
        mainThreadJson = buildJson()
        serviceThreadJson = buildJson()
    }

    private fun buildJson() = Json {
        serializersModule = SerializersModule {
            addMinestomSerializers(this)
            for ((_, action) in clazzToAddSerializerActionMap) {
                action(this)
            }
        }
    }
}

inline fun <reified T : PlayerData> PlayerDataService.register() = register(T::class)

inline fun <reified T : PlayerData> Player.getData(): T {
    return PlayerDataService.getPlayerData(this.uuid, T::class)
}

fun withOfflinePlayerData(username: String, action: OfflinePlayerDataContext.() -> Unit): Boolean {
    return PlayerDataService.withOfflinePlayerData(username, action)
}

fun withOfflinePlayerData(uuid: UUID, action: OfflinePlayerDataContext.() -> Unit): Boolean {
    return PlayerDataService.withOfflinePlayerData(uuid, action)
}

class OfflinePlayerDataContext(val uuid: UUID) {
    inline fun <reified T : PlayerData> getData(): T {
        return PlayerDataService.getPlayerData(uuid, T::class)
    }
}

val di = turingCoreDi