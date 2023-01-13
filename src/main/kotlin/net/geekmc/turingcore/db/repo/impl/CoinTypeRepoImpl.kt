package net.geekmc.turingcore.db.repo.impl

import net.geekmc.turingcore.db.entity.CoinType
import net.geekmc.turingcore.db.repo.CoinTypeRepo
import net.geekmc.turingcore.db.table.coinTypes
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*

/**
 * @see [CoinTypeRepo]
 */
class CoinTypeRepoImpl(private val db: Database) : CoinTypeRepo {
    override fun findAllCoinTypes(): Result<List<CoinType>> = runCatching {
        db.coinTypes.toList()
    }

    override fun findCoinTypeById(id: String): Result<CoinType> = runCatching {
        db.coinTypes.find { it.id eq id }
            ?: throw IllegalArgumentException("Coin type with id $id not found")
    }

    override fun findCoinTypesByName(name: String): Result<List<CoinType>> = runCatching {
        db.coinTypes.filter { it.name eq name }.toList()
    }

    override fun addCoinType(block: CoinType.() -> Unit): Result<CoinType> = runCatching {
        CoinType.invoke(block).also {
            db.coinTypes.add(it)
        }
    }

    override fun updateCoinType(coinType: CoinType): Result<Int> = runCatching {
        db.coinTypes.update(coinType)
    }

    override fun deleteCoinType(coinType: CoinType): Result<Int> = runCatching {
        coinType.delete()
    }
}