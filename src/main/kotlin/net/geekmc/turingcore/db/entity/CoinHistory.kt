package net.geekmc.turingcore.db.entity

import net.geekmc.turingcore.db.table.CoinHistoryActionType
import org.ktorm.entity.Entity
import java.time.Instant

interface CoinHistory: Entity<CoinHistory> {
    companion object : Entity.Factory<CoinHistory>()

    var player: PlayerUuid
    var coinType: CoinType
    var actionType: CoinHistoryActionType
    var before: Long
    var amount: Long
    var time: Instant
    var reason: String
}