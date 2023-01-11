package net.geekmc.turingcore.db.table

import net.geekmc.turingcore.db.entity.CoinHistory
import net.geekmc.turingcore.db.type.binaryUuid
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.text
import org.ktorm.schema.long
import org.ktorm.schema.timestamp

/**
 * 存储货币历史
 *
 * @see [CoinHistory]
 */
object CoinHistories : Table<CoinHistory>("t_coin_history") {
    val id = long("id").primaryKey()
    val playerUuid = binaryUuid("player_uuid").references(PlayerUuids) { it.player }
    val typeId = text("type_id").references(CoinTypes) { it.type }
    val amount = long("amount").bindTo { it.amount }
    val time = timestamp("time").bindTo { it.time }
    val reason = text("reason").bindTo { it.reason }
}

val Database.coinHistories get() = this.sequenceOf(CoinHistories)