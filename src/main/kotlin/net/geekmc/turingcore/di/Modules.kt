@file:Suppress("RemoveExplicitTypeArguments")

package net.geekmc.turingcore.di

import net.geekmc.turingcore.coin.CoinYamlConfig
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
import net.minestom.server.extensions.Extension
import org.kodein.di.DI
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.ktorm.database.Database
import org.slf4j.Logger
import java.nio.file.Path

enum class PathKey {
    DATA_FOLDER,
}

val baseModule by DI.Module {
    bindSingleton<Logger> { instance<Extension>().logger }
    bindSingleton<Path>(tag = PathKey.DATA_FOLDER) { instance<Extension>().dataDirectory }
}

val dbModule by DI.Module {
    bindEagerSingleton<Database> { createDatabase(instance(), instance()) }

    bindSingleton<PlayerUuidRepo> { PlayerUuidRepoImpl(instance()) }
    bindSingleton<CoinTypeRepo> { CoinTypeRepoImpl(instance()) }
    bindSingleton<CoinAmountRepo> { CoinAmountRepoImpl(instance(), instance(), instance()) }
    bindSingleton<CoinHistoryRepo> { CoinHistoryRepoImpl(instance()) }
}

val configModule by DI.Module {
    bindSingleton<DbYamlConfig> { DbYamlConfig.getInstance(instance(PathKey.DATA_FOLDER)) }
    bindSingleton<CoinYamlConfig> { CoinYamlConfig.getInstance(instance(PathKey.DATA_FOLDER)) }
}