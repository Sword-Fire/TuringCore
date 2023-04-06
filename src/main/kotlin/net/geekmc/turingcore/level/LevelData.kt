package net.geekmc.turingcore.level

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.data.player.PlayerData
import net.geekmc.turingcore.library.framework.AutoRegister

@AutoRegister(id = "TuringCore:LevelData")
@Serializable
data class LevelData(
    // Map 使用等级系统名称作为 key，等级数据作为 value
    val levels: HashMap<String, Level> = HashMap(),
) : PlayerData()

@Serializable
data class Level(
    var level: Long,
    var exp: Long
)