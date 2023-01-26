package net.geekmc.turingcore

import net.geekmc.turingcore.db.repo.CoinAmountRepo
import net.geekmc.turingcore.db.repo.CoinHistoryRepo
import net.geekmc.turingcore.db.repo.CoinTypeRepo
import net.geekmc.turingcore.db.repo.PlayerUuidRepo
import net.geekmc.turingcore.di.TuringCoreDIAware
import org.kodein.di.instance

/**
 * TuringCore 对外提供的 API
 */
@Suppress("unused")
object TuringCoreApi : TuringCoreDIAware {
    /**
     * 数据库部分
     */
    object Database {
        val playerUuidRepo by instance<PlayerUuidRepo>()
        val coinAmountRepo by instance<CoinAmountRepo>()
        val coinHistoryRepo by instance<CoinHistoryRepo>()
        val coinTypeRepo by instance<CoinTypeRepo>()
    }
}