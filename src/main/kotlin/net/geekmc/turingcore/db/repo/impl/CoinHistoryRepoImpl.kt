package net.geekmc.turingcore.db.repo.impl

import net.geekmc.turingcore.db.entity.CoinHistory
import net.geekmc.turingcore.db.repo.CoinHistoryRepo
import net.geekmc.turingcore.db.table.coinHistories
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.toList
import java.util.*

/**
 * @see [CoinHistoryRepo]
 */
class CoinHistoryRepoImpl(private val db: Database) : CoinHistoryRepo {
    override fun findCoinHistoriesByPlayerUuid(playerUuid: UUID): Result<List<CoinHistory>> = runCatching {
        db.coinHistories.filter { it.playerUuid eq playerUuid }.toList()
    }

    override fun findCoinHistoriesByCoinType(coinType: String): Result<List<CoinHistory>> = runCatching {
        db.coinHistories.filter { it.coinTypeId eq coinType }.toList()
    }

    override fun addCoinHistory(block: CoinHistory.() -> Unit): Result<CoinHistory> = runCatching {
        CoinHistory.invoke(block).also { db.coinHistories.add(it) }
    }

    override fun deleteCoinHistory(coinHistory: CoinHistory): Result<Int> = runCatching {
        coinHistory.delete()
    }
}