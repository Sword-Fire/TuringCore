package net.geekmc.turingcore.db.table

import net.geekmc.turingcore.db.entity.PlayerUuid
import net.geekmc.turingcore.db.type.binaryUuid
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
    val uuid = binaryUuid("uuid").primaryKey().bindTo { it.uuid }
    val name = text("name").bindTo { it.name }
}

val Database.playerUuids get() = this.sequenceOf(PlayerUuids)
