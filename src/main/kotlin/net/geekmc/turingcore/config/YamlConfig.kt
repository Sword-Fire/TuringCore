package net.geekmc.turingcore.config

import kotlinx.serialization.Serializable

/**
 * Yaml 配置类必须继承[YamlConfig]，且不允许有泛型参数。
 */
@Serializable
abstract class YamlConfig {
}