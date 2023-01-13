package net.geekmc.turingcore.db.repo

import net.geekmc.turingcore.db.entity.CoinHistory
import net.geekmc.turingcore.db.repo.impl.CoinHistoryRepoImpl
import net.geekmc.turingcore.db.table.CoinHistories
import java.util.*

/**
 * 货币历史仓库
 *
 * @see [CoinHistory]
 * @see [CoinHistories]
 * @see [CoinHistoryRepoImpl]
 */
interface CoinHistoryRepo {
    fun findCoinHistoriesByPlayerUuid(playerUuid: UUID): Result<List<CoinHistory>>
    fun findCoinHistoriesByCoinType(coinType: String): Result<List<CoinHistory>>
    fun addCoinHistory(block: CoinHistory.() -> Unit): Result<CoinHistory>
    fun deleteCoinHistory(coinHistory: CoinHistory): Result<Int>
}