package net.geekmc.turingcore.db.table

import net.geekmc.turingcore.db.entity.PlayerUuid
import net.geekmc.turingcore.db.type.textUuid
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.text

/**
 * 存储 玩家 与 UUID 的一对一映射
 *
 * @see [PlayerUuid]
 */
object PlayerUuids : Table<PlayerUuid>("t_player_uuid") {
    val playerName = text("player_name").primaryKey().bindTo { it.playerName }
    val playerUuid = textUuid("player_uuid").bindTo { it.playerUuid }
}

val Database.playerUuids get() = this.sequenceOf(PlayerUuids)
