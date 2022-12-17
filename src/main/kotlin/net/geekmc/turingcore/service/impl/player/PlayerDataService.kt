package net.geekmc.turingcore.service.impl.player

import com.extollit.gaming.ai.path.model.PathObject.active
import net.geekmc.turingcore.data.json.JsonData
import net.geekmc.turingcore.service.MinestomService
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.GLOBAL_EVENT
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import java.nio.file.Path

/**
 * 玩家数据服务，能够为每个玩家提供一个 [SerializableData]。
 * 通过 [register] 方法注册服务，注册后会自动在玩家登陆时从关联的 Json 文件加载信息到内存成为 [JsonData]。
 * @param name 玩家数据服务的名称。仅用于输出调试信息。
 * @param pathProducer 用于生成玩家数据文件的路径。
 */
class PlayerDataService private constructor(private val name: String, val pathProducer: (Player) -> Path) :
    MinestomService() {

    companion object {

        fun register(id: String, eventNode: EventNode<Event>, pathProducer: (Player) -> Path) =
            PlayerDataService(id, pathProducer).apply {
                start(eventNode)
            }

    }

    private val playerNameToDataMap = mutableMapOf<String, JsonData>()

    private val loginFunctions = mutableListOf<PlayerLoginEvent.() -> Unit>()
    private val disconnectFunctions = mutableListOf<PlayerDisconnectEvent.() -> Unit>()

    private val loginListener = EventListener.of(PlayerLoginEvent::class.java) {
        // 先加载文件，后运行玩家进服时需要执行的操作。
        it.player.loadData()
        for (block in loginFunctions) {
            block(it)
        }
    }

    private val disconnectListener = EventListener.of(PlayerDisconnectEvent::class.java) {
        for (block in disconnectFunctions) {
            block(it)
        }
        it.player.saveData()
        it.player.unloadData()
    }

    override fun onEnable() {
        GLOBAL_EVENT.addListener(loginListener)
        GLOBAL_EVENT.addListener(disconnectListener)
    }

    override fun onDisable() {
        GLOBAL_EVENT.removeListener(loginListener)
        GLOBAL_EVENT.removeListener(disconnectListener)
    }

    /**
     * 获取玩家所关联的数据。
     */
    fun getPlayerData(player: Player): JsonData {
        if (!player.isDataLoaded()) {
            player.loadData()
        }
        return playerNameToDataMap[player.username]!!
    }

    /**
     * 使用[PlayerDataService]时，应通过onLogin / onDisconnect来提交需要在玩家登录/断开链接时执行的操作，而非使用监听器。
     * 这能保证先加载文件，再执行操作。
     */
    fun onLogin(block: PlayerLoginEvent.() -> Unit) {
        loginFunctions.add(block)
    }

    /**
     * @see onLogin
     */
    fun onDisconnect(block: PlayerDisconnectEvent.() -> Unit) {
        disconnectFunctions.add(block)
    }

    /**
     * 返回玩家关联的文件是否已被加载。
     */
    private fun Player.isDataLoaded(): Boolean {
        return playerNameToDataMap.containsKey(username)
    }

    /**
     * 加载玩家关联的文件。
     */
    private fun Player.loadData() {
        if (playerNameToDataMap.containsKey(username)) {
            throw IllegalStateException("(PlayerDataService) Player data cannot be loaded more than once. (Player Data Service name: $name)")
        }
        playerNameToDataMap[username] = JsonData.load(pathProducer(this))
    }

    /**
     * 卸载玩家关联的文件。
     */
    private fun Player.unloadData() {
        playerNameToDataMap.remove(username)
    }

    /**
     * 保存玩家关联的文件。
     */
    private fun Player.saveData() {
        playerNameToDataMap[username]!!.save()
    }
}