package net.geekmc.turingcore.db.repo.impl

import net.geekmc.turingcore.db.entity.CoinAmount
import net.geekmc.turingcore.db.repo.CoinAmountRepo
import net.geekmc.turingcore.db.repo.CoinTypeRepo
import net.geekmc.turingcore.db.repo.PlayerUuidRepo
import net.geekmc.turingcore.db.table.coinAmounts
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.update
import java.util.*

/**
 * @see [CoinAmountRepo]
 */
class CoinAmountRepoImpl(
    private val db: Database,
    private val playerUuidRepo: PlayerUuidRepo,
    private val coinTypeRepo: CoinTypeRepo
) : CoinAmountRepo {
    private fun findCoinAmountOrCreate(playerUuid: UUID, typeId: String): Result<CoinAmount> = runCatching {
        val player = playerUuidRepo.findPlayerUuidByUuid(playerUuid).getOrThrow()
        val type = coinTypeRepo.findCoinTypeById(typeId).getOrThrow()
        db.coinAmounts.find { (it.playerUuid eq playerUuid) and (it.typeId eq typeId) } ?: addCoinAmount {
            this.player = player
            this.type = type
            this.amount = 0
        }.getOrThrow()
    }

    override fun findCoinAmountsByUuid(playerUuid: UUID): Result<List<CoinAmount>> = runCatching {
        val allTypes = coinTypeRepo.findAllCoinTypes().getOrThrow()
        allTypes.map { findCoinAmountOrCreate(playerUuid, it.id).getOrThrow() }
    }

    override fun findCoinAmountByUuidAndType(playerUuid: UUID, typeId: String): Result<CoinAmount> = runCatching {
        findCoinAmountOrCreate(playerUuid, typeId).getOrThrow()
    }

    override fun addCoinAmount(block: CoinAmount.() -> Unit): Result<CoinAmount> = runCatching {
        CoinAmount.invoke(block).also {
            db.coinAmounts.add(it)
        }
    }

    override fun updateCoinAmount(coinAmount: CoinAmount): Result<Int> = runCatching {
        db.coinAmounts.update(coinAmount)
    }
}