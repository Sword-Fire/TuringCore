package net.geekmc.turingcore.player.essentialdata

import net.geekmc.turingcore.library.data.player.getData
import net.geekmc.turingcore.library.di.TuringCoreDIAware
import net.geekmc.turingcore.library.event.EventNodes
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.library.service.Service
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.inventory.PlayerInventory
import world.cepi.kstom.event.listenOnly

// TODO: 存储玩家所在Instance。
@AutoRegister
object EssentialPlayerDataService : Service(), TuringCoreDIAware {
    override fun onEnable() {
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