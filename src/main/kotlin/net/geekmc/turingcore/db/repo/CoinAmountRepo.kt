package net.geekmc.turingcore.db.repo

import net.geekmc.turingcore.db.entity.CoinAmount
import net.geekmc.turingcore.db.repo.impl.CoinAmountRepoImpl
import net.geekmc.turingcore.db.table.CoinAmounts
import java.util.*

/**
 * 货币数量仓库
 *
 * @see [CoinAmount]
 * @see [CoinAmounts]
 * @see [CoinAmountRepoImpl]
 */
interface CoinAmountRepo {
    fun findCoinAmountsByUuid(playerUuid: UUID): Result<List<CoinAmount>>
    fun findCoinAmountByUuidAndType(playerUuid: UUID, typeId: String): Result<CoinAmount>
    fun addCoinAmount(block: CoinAmount.() -> Unit): Result<CoinAmount>
    fun updateCoinAmount(coinAmount: CoinAmount): Result<Int>
}