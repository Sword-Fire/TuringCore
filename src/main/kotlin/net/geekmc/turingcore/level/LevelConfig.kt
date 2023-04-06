package net.geekmc.turingcore.level

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.config.Config

@Serializable
data class LevelConfig(
    // 等级系统名称作为 key 等级表作为 value
    val levels: HashMap<String, LevelTable> = HashMap()
) : Config()

/**
 * 等级表
 * @param main 是否为主等级表
 * @param shadow 经验值与等级映射表
 */
@Serializable
data class LevelTable(
    val main: Boolean,
    val shadow: HashMap<Long, Long> = HashMap()
)
