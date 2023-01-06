package net.geekmc.turingcore.config

import kotlin.reflect.KProperty

interface BaseYamlObject

interface BaseYamlConfig {
    val yamlObj: BaseYamlObject
}

class YamlProperty<T>(
    private val yamlKey: KProperty<T?>,
    private val defaultValue: T? = null
) {
    operator fun getValue(thisRef: BaseYamlConfig, property: KProperty<*>): T {
        val yamlObj = thisRef.yamlObj
        // TODO: 寻找避免 ?: 嵌套地狱的方法...
        return yamlKey.getter.call(yamlObj)
            ?: (defaultValue ?: error("No value for ${property.name}"))
    }
}

inline fun <reified T> yaml(
    yamlKey: KProperty<T?>,
    defaultValue: T? = null
) = YamlProperty(yamlKey, defaultValue)