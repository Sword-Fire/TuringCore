@file:Suppress("RemoveExplicitTypeArguments")

package net.geekmc.turingcore.di

import net.geekmc.turingcore.db.DbYamlConfig
import net.geekmc.turingcore.db.createDatabase
import net.geekmc.turingcore.db.repo.CoinAmountRepo
import net.geekmc.turingcore.db.repo.CoinHistoryRepo
import net.geekmc.turingcore.db.repo.CoinTypeRepo
import net.geekmc.turingcore.db.repo.PlayerUuidRepo
import net.geekmc.turingcore.db.repo.impl.CoinAmountRepoImpl
import net.geekmc.turingcore.db.repo.impl.CoinHistoryRepoImpl
import net.geekmc.turingcore.db.repo.impl.CoinTypeRepoImpl
import net.geekmc.turingcore.db.repo.impl.PlayerUuidRepoImpl
import net.geekmc.turingcore.service.coin.CoinYamlConfig
import org.kodein.di.DI
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.ktorm.database.Database

val baseModule by DI.Module {

}

val dbModule by DI.Module {
    bindEagerSingleton<Database> { createDatabase(instance()) }

    bindSingleton<PlayerUuidRepo> { PlayerUuidRepoImpl(instance()) }
    bindSingleton<CoinTypeRepo> { CoinTypeRepoImpl(instance()) }
    bindSingleton<CoinAmountRepo> { CoinAmountRepoImpl(instance(), instance(), instance()) }
    bindSingleton<CoinHistoryRepo> { CoinHistoryRepoImpl(instance()) }
}

val configModule by DI.Module {
    bindSingleton<DbYamlConfig> { DbYamlConfig.getInstance() }
    bindSingleton<CoinYamlConfig> { CoinYamlConfig.getInstance() }
}