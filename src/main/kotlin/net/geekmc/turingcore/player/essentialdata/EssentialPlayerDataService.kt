package net.geekmc.turingcore.player.essentialdata

import net.geekmc.turingcore.data.player.PlayerDataService
import net.geekmc.turingcore.data.player.getData
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.service.Service
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.ItemStack
import world.cepi.kstom.event.listenOnly

// TODO: 存储玩家所在Instance。
object EssentialPlayerDataService : Service() {

    override fun onEnable() {
        PlayerDataService.register<EssentialPlayerData>()
        EventNodes.VERY_HIGH.listenOnly<PlayerLoginEvent> {
            val data = player.getData<EssentialPlayerData>()
            with(player) {
                respawnPoint = data.position
                gameMode = data.gameMode
                health = data.health
                data.permissions.forEach { addPermission(it) }
                for (i in 0 until data.inventory.size) {
                    inventory.setItemStack(i, data.inventory[i])
                }
            }
        }
        EventNodes.VERY_LOW.listenOnly<PlayerDisconnectEvent> {
            val data = player.getData<EssentialPlayerData>()
            with(data) {
                position = player.position
                gameMode = player.gameMode
                health = player.health
                permissions = HashSet(player.allPermissions)
                inventory = ArrayList<ItemStack>().apply {
                    for (i in 0 until PlayerInventory.INVENTORY_SIZE)
                        add(player.inventory.getItemStack(i))
                }
            }
        }
    }

}