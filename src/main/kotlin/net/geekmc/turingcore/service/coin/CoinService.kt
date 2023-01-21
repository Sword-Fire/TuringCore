package net.geekmc.turingcore.service.coin

import net.geekmc.turingcore.service.Service

object CoinService : Service(){

    override fun onEnable() {
    }

    override fun onDisable() {
        error("CoinService cannot be disabled!")
    }
}