package net.geekmc.turingcore.player.deprecated

import net.geekmc.turingcore.player.data
import net.geekmc.turingcore.service.EventService
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.permission.Permission
import world.cepi.kstom.event.listenOnly
import java.nio.file.Path

/**
 * 在玩家离线后存储玩家位置、血量、权限等信息，并在玩家登录时读取。
 */
object DeprecatedPlayerBasicDataService : EventService() {

    lateinit var service: DeprecatedPlayerDataService
        private set

    override fun onEnable() {
        // 注册服务。
        service = DeprecatedPlayerDataService.register("turingcore.basic", eventNode) {
            Path.of("PlayerData/${it.username}.json")
        }
        service.onLogin {
            read(player)
        }
        service.onDisconnect {
            save(player)
        }
        // 保存玩家下线时的位置。
        eventNode.listenOnly<PlayerSpawnEvent> {
            if (!isFirstSpawn) return@listenOnly
            player.data.get<Pos>("position")?.let { player.teleport(it) }
        }
    }

    override fun onDisable() {}

    // TODO: 补充药水效果等代码。
    fun read(player: Player) {
        // 血量。
        player.data.get<Float>("health")?.let { player.health = it }
        // 坐标。

        // TODO 待修改并删去
        // 管理员判断，这里的代码我没看懂。
        if (player.data.get<Boolean>("isOp") == null) {
            player.data["isOp"] = false
        }
        // 游戏模式。
        player.gameMode = player.data["gameMode"] ?: GameMode.ADVENTURE
        // 权限。
        player.data.get<ArrayList<Permission>>("permissions", arrayListOf()).forEach {
            player.addPermission(it)
        }
        // 背包物品。
        for (slot in 0 until player.inventory.size) {
            player.inventory.setItemStack(slot, player.data["inventory.$slot"] ?: continue)
        }
    }

    fun save(player: Player) {
        player.data["health"] = player.health
        player.data["position"] = player.position
        player.data["gameMode"] = player.gameMode
        player.data["permissions"] = arrayListOf<Permission>().apply {
            addAll(player.allPermissions)
        }
        // 即使物品为空也应该写入至文件，不然会导致刷物品。
        for (i in 0 until player.inventory.size) {
            val item = player.inventory.getItemStack(i)
            player.data["inventory.$i"] = item
        }
    }
}