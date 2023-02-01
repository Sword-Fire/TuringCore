package net.geekmc.turingcore.library.config

import kotlinx.serialization.Serializable

/**
 * 配置类必须继承 [Config] ，带有 data 数据类修饰符，使用 [Serializable] 标记，主构造函数所有参数必须为具有默认值的属性，且不允许有泛型参数。
 */
@Serializable
abstract class Config {
    /**
     * 保存配置文件。
     */
    fun save() {
        ConfigService.saveConfig(this)
    }
}