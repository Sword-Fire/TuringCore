package net.geekmc.turingcore.level

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.config.Config

@Serializable
data class LevelConfig(
    val levels: HashMap<Long, Long> = HashMap()
) : Config()
