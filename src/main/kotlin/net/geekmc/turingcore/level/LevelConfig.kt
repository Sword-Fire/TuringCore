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
 * @param display 是否在经验条显示等级
 * @param type 等级计算类型
 * @param shadow 经验值与等级映射表
 * @param formula 计算值与等级映射表公式
 * @param maxLevel 等级上限
 * @param maxExp 经验值上限
 */
@Serializable
data class LevelTable(
    val main: Boolean,
    val display: Boolean,
    val type: LevelType,
    val shadow: HashMap<Int, Int> = HashMap(),
    val formula: String,
    val maxLevel: Int,
    val maxExp: Int
)

@Serializable
enum class LevelType {
    // 映射
    SHADOW,

    // 公式
    FORMULA
}
