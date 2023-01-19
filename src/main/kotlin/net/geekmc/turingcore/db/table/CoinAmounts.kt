package net.geekmc.turingcore.db.table

import net.geekmc.turingcore.db.entity.CoinAmount
import net.geekmc.turingcore.db.type.binaryUuid
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.text

/**
 * 存储玩家的货币数量
 *
 * @see [CoinAmount]
 */
object CoinAmounts: Table<CoinAmount>("t_coin_amount") {
    val id = int("id").primaryKey().bindTo { it.id }
    val playerUuid = binaryUuid("player_uuid").references(PlayerUuids) { it.player }
    val typeId = text("type_id").primaryKey().references(CoinTypes) { it.type }
    val amount = long("amount").bindTo { it.amount }
}

val Database.coinAmounts get() = this.sequenceOf(CoinAmounts)