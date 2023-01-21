package net.geekmc.turingcore.player

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.geekmc.turingcore.data.PlayerData
import net.geekmc.turingcore.data.PlayerDataService
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.service.Service
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.ItemStack
import net.minestom.server.permission.Permission
import world.cepi.kstom.event.listenOnly

// TODO 存储Instance。玩家要先设置Instance，后允许teleport。
object EssentialPlayerDataService : Service() {

    @Serializable
    data class EssentialPlayerData(
        var isOp: Boolean = false,
        var position: @Contextual Pos = Pos(0.0, 120.0, 0.0),
        var gameMode: GameMode = GameMode.ADVENTURE,
        var health: Float = 20f,
        var permissions: Set<@Contextual Permission> = setOf(),
        // 注意： player.inventory.size == INVENTORY_SIZE == 46 != INNER_INVENTORY_SIZE == 36
        var inventory: ArrayList<ItemStack> = ArrayList<ItemStack>(PlayerInventory.INVENTORY_SIZE).apply {
            fill(ItemStack.AIR)
        }
    ) : PlayerData()

    override fun onEnable() {
        PlayerDataService.register(EssentialPlayerData::class)
        EventNodes.VERY_HIGH.listenOnly<PlayerLoginEvent> {
            val data = PlayerDataService.getData<EssentialPlayerData>(player)
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
        // TODO: on Inventory Click 验证点击的所有槽，是不是有0-45？
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