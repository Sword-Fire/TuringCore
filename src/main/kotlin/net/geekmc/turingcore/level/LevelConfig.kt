package net.geekmc.turingcore.level

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.config.Config

@Serializable
data class LevelConfig(
    val levels: Set<LevelTable> = HashSet()
) : Config()

/**
 * 等级表
 * @param name 等级表名称
 * @param main 是否为主等级表
 * @param shadow 经验值与等级映射表
 */
@Serializable
data class LevelTable(
    val name: String,
    val main: Boolean,
    val shadow: Map<Long, Long> = HashMap()
)
