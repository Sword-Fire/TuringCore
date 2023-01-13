package net.geekmc.turingcore.db.repo

import net.geekmc.turingcore.db.entity.CoinType
import net.geekmc.turingcore.db.repo.impl.CoinTypeRepoImpl
import net.geekmc.turingcore.db.table.CoinTypes

/**
 * 货币类型仓库
 *
 * @see [CoinType]
 * @see [CoinTypes]
 * @see [CoinTypeRepoImpl]
 */
interface CoinTypeRepo {
    fun findAllCoinTypes(): Result<List<CoinType>>
    fun findCoinTypeById(id: String): Result<CoinType>
    fun findCoinTypesByName(name: String): Result<List<CoinType>>
    fun addCoinType(block: CoinType.() -> Unit): Result<CoinType>
    fun updateCoinType(coinType: CoinType): Result<Int>
    fun deleteCoinType(coinType: CoinType): Result<Int>
}