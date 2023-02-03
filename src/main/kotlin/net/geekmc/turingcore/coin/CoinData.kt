package net.geekmc.turingcore.coin

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.data.player.PlayerData
import net.geekmc.turingcore.library.framework.AutoRegister

@AutoRegister(id = "TuringCore:CoinData")
@Serializable
data class CoinData(
    val coins: HashMap<String, Long> = HashMap(),
) : PlayerData()