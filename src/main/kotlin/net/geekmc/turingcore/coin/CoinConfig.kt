package net.geekmc.turingcore.coin

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.config.Config

@Serializable
data class CoinConfig(
    val coins: HashMap<String, CoinDisplay> = HashMap()
) : Config()

@Serializable
data class CoinDisplay(
    val name: String,
    val color: String
)