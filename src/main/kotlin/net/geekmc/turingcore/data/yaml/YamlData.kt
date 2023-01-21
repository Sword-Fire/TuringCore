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

        // 用于解析的默认 Yaml 对象。
        private val defaultYaml: Yaml

        // 将文件从内存存放到硬盘的默认设置。
        private val defaultDumperOptions = DumperOptions()

        init {
            defaultDumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            defaultYaml = Yaml(defaultDumperOptions)
        }
    }

    private val path: Path
    var rootMapObject: LinkedHashMap<Any, Any>
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
        rootMapObject = if (!path.exists()) {
            LinkedHashMap()
        } else {
            yaml.load(FileReader(path.absolutePathString())) ?: LinkedHashMap()
        }
    }

    /**
     * 根据提供的字符串形式表示的由.分隔的键，获取对应的值。每个键首先会被转换为字符串对象，以尝试从Yaml文件中读取对应值；
     * 若无法读取到值，则尝试将键转换为 Int 对象以读取对应值；若仍读取不到值，返回null。调用示例: get("a.123.c")
     */
    operator fun <T> get(keysSplitByDoc: String): T? {
        // 当前的 MutableMap 对象。
        var currentMapObject = rootMapObject
        val iter = keysSplitByDoc.split(".").iterator()
        while (iter.hasNext()) {
            val key = iter.next()
            // 若将键转换为字符串对象以读取对应值失败，则转换为 Int 对象尝试读取值。
            if (currentMapObject[key] == null) {
                // TODO 待测试正确性
                val keyAsInt: Int = runCatching { key.toInt() }.getOrElse { return null }
                if (currentMapObject[keyAsInt] == null) {
                    return null
                }
            }
            if (!iter.hasNext()) {
                @Suppress("UNCHECKED_CAST")
                return currentMapObject[key] as? T
            } else {
                @Suppress("UNCHECKED_CAST")
                currentMapObject = currentMapObject[key] as? LinkedHashMap<Any, Any> ?: return null
            }
        }
        return null
    }

    fun <T> get(key: String, defaultValue: T): T {
        return get(key) ?: defaultValue
    }

//    fun getKeys(deep: Boolean): Set<String> {
//        val keys = hashSetOf<String>()
//
//        @Suppress("UNCHECKED_CAST")
//        fun process(map: Map<String, Any?>, parent: String = "") {
//            map.forEach { (k, v) ->
//                if (v is MutableMap<*, *>) {
//                    if (deep) {
//                        process(v as LinkedHashMap<String, Any?>, "$parent$k.")
//                    } else {
//                        keys += "$parent$k"
//                    }
//                } else {
//                    keys += "$parent$k"
//                }
//            }
//        }
//        process(rootMapObject)
//        return keys
//    }

    /**
     * 存在潜在问题：纯数字的键会被转换成字符串对象再被用于访问Map，导致文件中所有数字键会被加上双引号。
     */
    @Deprecated("Yaml file not support editing anymore.")
    operator fun <T> set(keysSplitByDoc: String, value: T): Boolean {
        var currentMapObject = rootMapObject
        val iter = keysSplitByDoc.split(".").iterator()
        while (iter.hasNext()) {
            val next = iter.next()
            if (iter.hasNext()) {
                // 尝试寻找已经存在的 Map，若不存在则创建。
                @Suppress("UNCHECKED_CAST")
                currentMapObject = currentMapObject[next] as? LinkedHashMap<Any, Any> ?: let {
                    currentMapObject[next] = LinkedHashMap<Any, Any>()
                    currentMapObject[next] as LinkedHashMap<Any, Any>
                }
            } else {
                currentMapObject[next] = value ?: error("Value cannot be null.")
                return true
            }
        }
        return false
    }

    fun save() {
        yaml.dump(rootMapObject, FileWriter(path.absolutePathString()))
    }
}
