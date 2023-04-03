package net.geekmc.turingcore.level

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.data.player.PlayerData
import net.geekmc.turingcore.library.framework.AutoRegister

@AutoRegister(id = "TuringCore:LevelData")
@Serializable
data class LevelData(
    val level: Long,
    val exp: Long
) : PlayerData()
