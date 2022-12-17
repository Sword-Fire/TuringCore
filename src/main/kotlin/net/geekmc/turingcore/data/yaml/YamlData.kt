package net.geekmc.turingcore.data.yaml

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.representer.Representer
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

/**
 * 代表了和一个 Yaml 文件相关联的，存储在内存中的数据。
 * 不支持 Minestom 对象，仅支持原生数据类型。
 * 不建议用来保存数据。（仅作为配置文件使用）
 */
class YamlData(path: Path, yaml: Yaml = defaultYaml) {

    companion object {

        private val defaultYaml: Yaml
        private val defaultDumperOptions = DumperOptions()

        init {
            defaultDumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            defaultYaml = Yaml(defaultDumperOptions)
        }
    }

    private val path: Path
    var rootMap: MutableMap<String, Any>
        private set
    private var yaml: Yaml

    constructor(path: Path, clazz: Class<*>) : this(
        path,
        Yaml(CustomClassLoaderConstructor(clazz.classLoader), Representer(), defaultDumperOptions)
    )

    constructor(path: Path, loader: ClassLoader) : this(
        path,
        Yaml(CustomClassLoaderConstructor(loader), Representer(), defaultDumperOptions)
    )

    init {
        this.path = path
        this.yaml = yaml
        rootMap = if (!path.exists()) {
            LinkedHashMap()
        } else {
            yaml.load(FileReader(path.absolutePathString())) ?: LinkedHashMap()
        }
    }

    operator fun <T> get(key: String): T? {
        val keys = key.split(".")
        var obj = rootMap
        val iter = keys.iterator()
        while (iter.hasNext()) {
            // Next 是字符串
            var next: Any? = iter.next()
            // 以字符串对象作为 Key 尝试寻找对应的 Value 失败
            if (obj[next] == null) {
                try {
                    next = next.toString().toInt()
                } catch (exception: java.lang.NumberFormatException) {
                    exception.printStackTrace()
                    return null
                }
                // String 转成 Integer 作为 Key 未存在，返回空。
                // TODO 这里有问题？？？
                if (obj[next.toString()] == null) {
                    return null
                }
            }
            if (!iter.hasNext()) {
                @Suppress("UNCHECKED_CAST")
                return obj[next] as? T ?: return null
            } else {
                @Suppress("UNCHECKED_CAST")
                obj = obj[next] as? MutableMap<String, Any> ?: return null
            }
        }
        return null
    }

    fun <T> get(key: String, def: T): T {
        return get(key) ?: def
    }

    fun getKeys(deep: Boolean): Set<String> {
        val keys = hashSetOf<String>()

        @Suppress("UNCHECKED_CAST")
        fun process(map: Map<String, Any?>, parent: String = "") {
            map.forEach { (k, v) ->
                if (v is MutableMap<*, *>) {
                    if (deep) {
                        process(v as MutableMap<String, Any?>, "$parent$k.")
                    } else {
                        keys += "$parent$k"
                    }
                } else {
                    keys += "$parent$k"
                }
            }
        }
        process(rootMap)
        return keys
    }

    operator fun <T> set(key: String, value: T): Boolean {
        val keys = key.split(".")
        var obj = rootMap
        val iter = keys.iterator()
        while (iter.hasNext()) {
            val next = iter.next()
            if (iter.hasNext()) {
                // 尝试寻找已经存在的 Map，若不存在则创建。
                @Suppress("UNCHECKED_CAST")
                obj = obj[next] as? MutableMap<String, Any> ?: let {
                    obj[next] = LinkedHashMap<String, Any>()
                    obj[next] as MutableMap<String, Any>
                }
            } else {
                obj[next] = value ?: error("Value cannot be null.")
                return true
            }
        }
        return false
    }

    fun save() {
        yaml.dump(rootMap, FileWriter(path.absolutePathString()))
    }
}