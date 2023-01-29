package net.geekmc.turingcore.player.essentialdata

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.geekmc.turingcore.data.player.PlayerData
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.ItemStack
import net.minestom.server.permission.Permission

@Serializable
data class EssentialPlayerData(
    var isOp: Boolean = false,
    var position: @Contextual Pos = Pos(0.0, 120.0, 0.0),
    var gameMode: GameMode = GameMode.ADVENTURE,
    var health: Float = 20f,
    var permissions: Set<@Contextual Permission> = setOf(),
    // 注意： player.inventory.size == INVENTORY_SIZE == 46 != INNER_INVENTORY_SIZE == 36
    var inventory: MutableList<ItemStack> = MutableList(PlayerInventory.INVENTORY_SIZE) { ItemStack.AIR }
) : PlayerData()