package net.geekmc.turingcore.service.coin

import net.geekmc.turingcore.db.entity.CoinType
import net.geekmc.turingcore.db.table.coinTypes
import net.geekmc.turingcore.di.DITuringCoreAware
import net.geekmc.turingcore.service.MinestomService
import net.geekmc.turingcore.util.info
import org.kodein.di.instance
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.toList

object CoinService : MinestomService(){

    override fun onEnable() {
    }

    override fun onDisable() {
        error("CoinService cannot be disabled!")
    }
}