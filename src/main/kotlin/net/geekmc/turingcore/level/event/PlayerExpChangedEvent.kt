package net.geekmc.turingcore.level.event

import net.minestom.server.entity.Player
import net.minestom.server.event.Event

/**
 * 玩家经验值变化事件
 *
 * @param player 玩家
 * @param type 等级表名称
 * @param original 变化前的玩家经验值
 * @param exp 变化后的玩家经验值
 */
data class PlayerExpChangedEvent(
    val player: Player,
    val type: String,
    val original: Long,
    val exp: Long
) : Event {}