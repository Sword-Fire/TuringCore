package net.geekmc.turingcore.db.entity

import org.ktorm.entity.Entity

interface CoinAmount: Entity<CoinAmount> {
    companion object : Entity.Factory<CoinAmount>()

    var player: PlayerUuid
    var type: CoinType
    var amount: Long
}