package net.geekmc.turingcore.db.table

import net.geekmc.turingcore.db.entity.CoinType
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.text

/**
 * 存储货币类型
 *
 * @see [CoinType]
 */
object CoinTypes : Table<CoinType>("t_coin_type") {
    val id = text("id").primaryKey().bindTo { it.id }
    val name = text("name").bindTo { it.name }
}

val Database.coinTypes get() = this.sequenceOf(CoinTypes)