package net.geekmc.turingcore.data.player

import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import net.geekmc.turingcore.data.json.JsonData.Companion.SERIALIZATION_JSON
import net.geekmc.turingcore.data.player.PlayerDataService.getDataOfPlayer
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
import kotlin.reflect.full.createType
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * 获取玩家的某一类型的数据。只允许在主线程中使用。
 * 警告：在你的代码段运行完毕、让出主线程控制权后，通过该方法获取的引用可能在任何时候失效。
 * 因此，当你在某一时刻获取了主线程控制权，总是通过 [getDataOfPlayer] 方法来获取新的引用，且不要存放这个引用。
 */
inline fun <reified T : PlayerData> Player.getData(): T {
    return getDataOfPlayer(this)
}

private typealias PlayerDataMap = HashMap<String, PlayerData>

// TODO: 允许加载离线玩家的数据。
/**
 * 玩家数据服务。
 */
object PlayerDataService : Service() {

    // 存储所有玩家数据的Map，只允许在主线程中访问。
    val uuidToDataMap = HashMap<UUID, PlayerDataMap>()
    val clazzToIdentifierMap = HashMap<KClass<*>, String>()

    /**
     * 用于玩家数据读写的协程域。单线程以保证不会出现并发读写问题。
     * "从文件读取玩家数据并构建玩家的所有 PlayerData "，与"将玩家的所有 PlayerData 序列化并写入文件",这两个过程都应完全在此协程域进行。
     */
    @OptIn(DelicateCoroutinesApi::class)
    private val playerDataServiceContext = newSingleThreadContext("PlayerDataDispatcher")

    private val UUID.dataFile: Path
        @Suppress("spellCheckingInspection")
        get() = Path.of("playerdata/${this}.json")

    fun register(clazz: KClass<*>) {
        clazzToIdentifierMap[clazz] = clazz.qualifiedName!!
    }

    inline fun <reified T : PlayerData> getDataOfPlayer(p: Player): T {
        val identifier = clazzToIdentifierMap[T::class] ?: error("Identifier of Class ${T::class.qualifiedName} not exists")
        val playerDataMap = uuidToDataMap[p.uuid] ?: error("Can not find the data map of player ${p.username} with player uuid ${p.uuid} in uuidToDataMap")
        val data = playerDataMap[identifier] ?: error("Data with identifier $identifier not found in player ${p.username}")
        return data as? T ?: error("While getting data $identifier of player ${p.username}, failed to convert PlayerData to ${T::class.simpleName}")
    }

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

    /**
     * 加载玩家数据，会阻塞调用线程，因此应在异步线程中调用。
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
                    withContext(Dispatchers.MinestomSync) {
                        uuidToDataMap.remove(uuid)
                    }
                    return@withContext false
                }
                val fileContentMap: LinkedHashMap<String, String> = SERIALIZATION_JSON.decodeFromString(fileContent)
                val dataMap = PlayerDataMap()
                // 生成所有注册过的玩家数据。
                for ((clazz, identifier) in clazzToIdentifierMap) {
                    dataMap[identifier] = (SERIALIZATION_JSON.decodeFromString(
                        serializer(clazz.createType()),
                        fileContentMap[identifier] ?: "{}"
                    ) as PlayerData).apply { available = true }
                }
                withContext(Dispatchers.MinestomSync) {
                    uuidToDataMap[uuid] = dataMap
                }
                true
            }
        }
    }

    /**
     * 将玩家数据序列化，随后将序列化后的数据异步写入文件。
     */
    private fun savePlayerData(uuid: UUID) {
        // 在主线程将玩家数据固定到LinkedHashMap中
        val fileContentMap = LinkedHashMap<String, String>()
        for ((clazz, identifier) in clazzToIdentifierMap) {
            // 将玩家数据序列化为字符串
            fileContentMap[identifier] = SERIALIZATION_JSON.encodeToString(
                serializer(clazz.createType()),
                uuidToDataMap[uuid]!![identifier]!!
            )
        }
        // 在玩家数据线程写入文件
        CoroutineScope(playerDataServiceContext).launch {
            val file = uuid.dataFile
            if (file.notExists()) {
                file.parent.createDirectories()
                file.createFile()
            }
            val fileContent = SERIALIZATION_JSON.encodeToString(fileContentMap)
            file.writeText(fileContent)
        }
    }

}