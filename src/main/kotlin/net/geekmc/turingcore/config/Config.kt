package net.geekmc.turingcore.config

import kotlinx.serialization.Serializable

/**
 * 配置文件类必须继承 [Config] ，且不允许有泛型参数。
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