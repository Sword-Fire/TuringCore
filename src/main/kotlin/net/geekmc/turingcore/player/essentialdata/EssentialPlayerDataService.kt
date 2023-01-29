package net.geekmc.turingcore.player.essentialdata

import net.geekmc.turingcore.data.player.PlayerDataService
import net.geekmc.turingcore.data.player.getData
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.framework.AutoRegister
import net.geekmc.turingcore.service.Service
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.ItemStack
import world.cepi.kstom.event.listenOnly

// TODO: 存储玩家所在Instance。
@AutoRegister
object EssentialPlayerDataService : Service() {

    override fun onEnable() {
        PlayerDataService.register<EssentialPlayerData>()
        EventNodes.VERY_HIGH.listenOnly<PlayerLoginEvent> {
            val data = player.getData<EssentialPlayerData>()
            player.apply {
                respawnPoint = data.position
                gameMode = data.gameMode
                health = data.health
                data.permissions.forEach { addPermission(it) }
                data.inventory.forEachIndexed { index, itemStack ->
                    inventory.setItemStack(index, itemStack)
                }
            }
        }
        EventNodes.VERY_LOW.listenOnly<PlayerDisconnectEvent> {
            player.getData<EssentialPlayerData>().apply {
                position = player.position
                gameMode = player.gameMode
                health = player.health
                permissions = HashSet(player.allPermissions)
                inventory = MutableList(PlayerInventory.INVENTORY_SIZE) {
                    player.inventory.getItemStack(it)
                }
            }
        }
    }
}