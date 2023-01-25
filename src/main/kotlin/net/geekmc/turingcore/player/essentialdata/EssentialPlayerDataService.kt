package net.geekmc.turingcore.player.essentialdata

import net.geekmc.turingcore.data.PlayerDataService
import net.geekmc.turingcore.data.dataOf
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.service.Service
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.ItemStack
import world.cepi.kstom.event.listenOnly

// TODO 存储Instance。玩家要先设置Instance，后允许teleport。
object EssentialPlayerDataService : Service() {



    override fun onEnable() {
        PlayerDataService.register(EssentialPlayerData::class)
        EventNodes.VERY_HIGH.listenOnly<PlayerLoginEvent> {
            val data = dataOf<EssentialPlayerData>(player)
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
            val data = PlayerDataService.getData<EssentialPlayerData>(player)
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