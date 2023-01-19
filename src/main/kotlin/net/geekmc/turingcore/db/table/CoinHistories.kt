package net.geekmc.turingcore.db.table

import net.geekmc.turingcore.db.entity.CoinHistory
import net.geekmc.turingcore.db.type.binaryUuid
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*

enum class CoinHistoryActionType {
    ADD,
    REMOVE,
    SET
}

/**
 * 存储货币历史
 *
 * @see [CoinHistory]
 */
@Suppress("unused")
object CoinHistories : Table<CoinHistory>("t_coin_history") {
    val id = long("id").primaryKey()
    val playerUuid = binaryUuid("player_uuid").references(PlayerUuids) { it.player }
    val coinTypeId = text("coin_type_id").references(CoinTypes) { it.coinType }
    val actionType = enum<CoinHistoryActionType>("action_type").bindTo { it.actionType }
    val before = long("before").bindTo { it.before }
    val amount = long("amount").bindTo { it.amount }
    val time = timestamp("time").bindTo { it.time }
    val reason = text("reason").bindTo { it.reason }
}

val Database.coinHistories get() = this.sequenceOf(CoinHistories)