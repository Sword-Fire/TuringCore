package net.geekmc.turingcore.reputation

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.config.Config

@Serializable
data class ReputationConfig(
    val reputations: HashMap<String, ReputationTable> = HashMap()
) : Config()

@Serializable
data class ReputationTable(
    val name: String,
    val level: HashMap<Int, Int> = HashMap(),
    val min: Int,
    val max: Int,
    val levelName: HashMap<Int, String> = HashMap(),
    val relative: HashMap<String, Int> = HashMap()
)