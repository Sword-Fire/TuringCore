package net.geekmc.turingcore.db.entity

import org.ktorm.entity.Entity

interface CoinType: Entity<CoinType> {
    companion object : Entity.Factory<CoinType>()

    var id: String
    var name: String
}