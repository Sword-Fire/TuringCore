package net.geekmc.turingcore.level.event

import net.minestom.server.entity.Player
import net.minestom.server.event.Event

/**
 * 玩家等级变化事件
 *
 * @param player 玩家
 * @param type 等级表名称
 * @param original 变化前的玩家等级
 * @param level 变化后的玩家等级
 */
data class PlayerLevelChangedEvent(
    val player: Player,
    val type: String,
    val original: Long,
    val level: Long
) : Event {}