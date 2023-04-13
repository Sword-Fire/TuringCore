package net.geekmc.turingcore.reputation

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.data.player.PlayerData
import net.geekmc.turingcore.library.framework.AutoRegister

@Serializable
@AutoRegister(id = "TuringCore:ReputationData")
data class ReputationData(
    val reputations: HashMap<String, Int> = HashMap()
) : PlayerData()
