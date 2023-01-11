package net.geekmc.turingcore.db.entity

import org.ktorm.entity.Entity
import java.time.Instant

interface CoinHistory: Entity<CoinHistory> {
    companion object : Entity.Factory<CoinHistory>()

    var player: PlayerUuid
    var type: CoinType
    var amount: Long
    var time: Instant
    var reason: String
}