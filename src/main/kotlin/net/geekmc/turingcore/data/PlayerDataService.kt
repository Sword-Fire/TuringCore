package net.geekmc.turingcore.data


import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import net.geekmc.turingcore.TuringCore
import net.geekmc.turingcore.data.json.JsonData.Companion.SERIALIZATION_JSON
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.player.EssentialPlayerDataService
import net.geekmc.turingcore.player.PlayerUUIDProvider
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.timeOf
import net.minestom.server.entity.Player
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.ItemStack
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly
import java.nio.file.Path
import java.time.Duration
import java.util.*
import kotlin.io.path.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

inline fun <reified T : PlayerData> dataOf(player: Player): T {
    return PlayerDataService.getData(player)
}

object PlayerDataService : Service() {

    val uuidToDataMap = HashMap<UUID, HashMap<String, PlayerData>>()
    val clazzToIdentifierMap = HashMap<KClass<*>, String>()

    /**
     * 用于玩家数据读写的协程域。单线程以保证不会出现并发读写问题。
     */
    @OptIn(DelicateCoroutinesApi::class)
    private val playerDataServiceContext = newSingleThreadContext("PlayerDataDispatcher")

    private val Player.dataFile: Path
        @Suppress("spellCheckingInspection")
        get() = Path.of("playerdata/${uuid}.json")

    fun register(clazz: KClass<*>) {
        clazzToIdentifierMap[clazz] = clazz.qualifiedName!!
    }

    /**
     * 获取玩家的某一类型的数据。
     * 警告：在你的代码段运行完毕、让出主线程控制权后，通过该方法获取的引用可能在任何时候失效。
     * 因此，当你在某一时刻获取了主线程控制权，总是通过[getData]方法来获取新的引用，且不要存放这个引用。
     */
    inline fun <reified T : PlayerData> getData(p: Player): T {
        val identifier = clazzToIdentifierMap[T::class] ?: error("Identifier of Class ${T::class.qualifiedName} not exists")
        val playerDataMap = uuidToDataMap[p.uuid] ?: error("Can not find the data map of player ${p.username} with player uuid ${p.uuid} in uuidToDataMap")
        // 获取该Data的内容，以String表示
        val data = playerDataMap[identifier] ?: error("Data with identifier $identifier not found in player ${p.username}")
        return data as? T ?: error("While getting data $identifier of player ${p.username}, failed to convert PlayerData to ${T::class.simpleName}")
    }

    override fun onEnable() {
        // 进服时加载数据
        EventNodes.INTERNAL_HIGHEST.listenOnly<AsyncPlayerPreLoginEvent> {
            // TODO 考虑使用UUIDProvider代替此处设置UUID。
            // 在一切的之前更新uuid
            // 事件和玩家的uuid都要更改，猜测事件结束后会用事件 uuid 覆盖玩家 uuid
            playerUuid = PlayerUUIDProvider.getUUID(player)
            player.uuid = playerUuid
            // 必须等待加载数据后才能进服。
            runBlocking {
                // 允许有 3000 毫秒读取文件
                withTimeoutOrNull(5000L) {
                    withContext(playerDataServiceContext) {
                        // json文件的文本内容。
                        val fileContent = if (player.dataFile.exists()) player.dataFile.readText() else "{}"
                        // 将文本内容转换为 Identifier -> String Map
                        val fileContentMap: LinkedHashMap<String, String> = SERIALIZATION_JSON.decodeFromString(fileContent)
                        // 为玩家创建一个新Map
                        uuidToDataMap[player.uuid] = HashMap()

                        // 遍历所有注册过的玩家数据类型，将每个玩家数据类型的标识符作为 key 寻找对应的以 String 表示的数据，并将其转换为对应的数据类型。
                        // 转换过程中使用的反序列化器由 clazz 生成。
                        for ((clazz, identifier) in clazzToIdentifierMap) {
                            uuidToDataMap[player.uuid]!![identifier] = SERIALIZATION_JSON.decodeFromString(
                                serializer(clazz.createType()),
                                fileContentMap[identifier] ?: "{}"
                            ) as PlayerData
                        }
                    }
                } ?: run {
                    // 如果超时了，就直接踢出玩家
                    // TODO 验证在AsyncLogin中踢出玩家会不会触发DisconnectEvent
                    // 如果会触发，则需要在此处将玩家设置数据为空，在DisconnectEvent中检查如果为空时则不保存
                    uuidToDataMap.remove(player.uuid)
                    TuringCore.INSTANCE.logger.error("读取玩家 ${player.username} 的数据超时")
                    player.kick("读取玩家数据超时")
                }
            }
        }

        // 退出时保存。最低优先级。
        EventNodes.INTERNAL_LOWEST.listenOnly<PlayerDisconnectEvent> {
            player.saveData()
        }

        // 定时保存，延时30分钟后进行第一次检查，并以后每30分钟检查一次
        val saveInterval = Duration.ofMinutes(30)
        Manager.scheduler.buildTask {
            // 切到主线程
            val time = timeOf { Manager.connection.onlinePlayers.forEach { it.saveData() } }
            TuringCore.INSTANCE.logger.info("定时保存玩家数据，耗时 $time ms")
        }.delay(saveInterval).repeat(saveInterval).schedule()
    }

    private fun Player.saveData() {

        // 必须马上在主线程序列化数据，因为玩家退服后数据就会丢失。
        // 序列化数据写入文件可以稍后再做。
        val fileContentMap = LinkedHashMap<String, String>()
        // 遍历所有玩家数据类型
        for ((clazz, identifier) in clazzToIdentifierMap) {
            // 将玩家数据序列化为字符串
            fileContentMap[identifier] = SERIALIZATION_JSON.encodeToString(
                serializer(clazz.createType()),
                uuidToDataMap[this.uuid]!![identifier]!!
            )
        }
        // 在玩家数据线程写入文件
        CoroutineScope(playerDataServiceContext).launch {
            val file = this@saveData.dataFile
            if (!file.exists()) {
                file.parent.createDirectories()
                file.createFile()
            }
            val fileContent = SERIALIZATION_JSON.encodeToString(fileContentMap)
            file.writeText(fileContent)
        }
    }

}