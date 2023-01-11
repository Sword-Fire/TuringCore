package net.geekmc.turingcore.db.util

import net.geekmc.turingcore.db.db
import net.geekmc.turingcore.db.entity.CoinAmount
import net.geekmc.turingcore.db.entity.CoinType
import net.geekmc.turingcore.db.entity.PlayerUuid
import net.geekmc.turingcore.db.table.coinAmounts
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find

fun getCoinAmountOrCreate(player: PlayerUuid, type: CoinType): CoinAmount {
    return db.coinAmounts.find { (it.playerUuid eq player.uuid) and (it.typeId eq type.id) } ?: CoinAmount {
        this.player = player
        this.type = type
        this.amount = 0
    }.also { db.coinAmounts.add(it) }
}