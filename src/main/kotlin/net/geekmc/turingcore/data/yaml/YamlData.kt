package net.geekmc.turingcore.data.yaml

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.representer.Representer
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Path
import kotlin.io.path.absolutePathString

/**
 * 代表了和一个 Yaml 文件相关联的，存储在内存中的数据。
 * 不支持 Minestom 对象，仅支持原生数据类型。
 * 不建议用来保存数据。（仅作为配置文件使用）
 */
class YamlData(private val path: Path, private val yaml: Yaml = defaultYaml) {

    companion object {
        // 将文件从内存存放到硬盘的默认设置。
        private val defaultDumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        }

        // 用于解析的默认 Yaml 对象。
        private val defaultYaml: Yaml = Yaml(defaultDumperOptions)
    }

    val rootMapObject: LinkedHashMap<Any, Any> = runCatching {
        yaml.load<LinkedHashMap<Any, Any>>(FileReader(path.absolutePathString()))
    }.getOrElse {
        // TODO: Logger
        LinkedHashMap()
    }

    constructor(path: Path, clazz: Class<*>) : this(path, clazz.classLoader)

    constructor(path: Path, loader: ClassLoader) : this(
        path,
        Yaml(CustomClassLoaderConstructor(loader), Representer(defaultDumperOptions), defaultDumperOptions)
    )

    private tailrec fun <T> getByKeys(keys: Iterator<String>, mapObject: LinkedHashMap<Any, Any>): T {
        val key = keys.next()
        val nextMapObject = mapObject.getOrDefault(key, null).let {
            it ?: (key.toIntOrNull()?.let { intKey ->
                // 若将键转换为字符串对象以读取对应值失败，则转换为 Int 对象尝试读取值。
                mapObject.getOrDefault(intKey, null)
            } ?: error("Value not found."))
        }
        @Suppress("UNCHECKED_CAST")
        return if (!keys.hasNext()) nextMapObject as T else getByKeys(keys, nextMapObject as LinkedHashMap<Any, Any>)
    }

    /**
     * 根据提供的键 [keysSplitByDoc]，获取对应的值。
     *
     * 每个键首先会被转换为字符串对象，以尝试从 Yaml 文件中读取对应值；
     *
     * 若无法读取到值，则尝试将键转换为 Int 对象以读取对应值；
     *
     * 若仍读取不到值，抛出 [IllegalStateException] 异常。
     *
     * 用例：`get("a.123.c")`
     *
     * @param [keysSplitByDoc] 由`.`分隔的键
     *
     * @throws [IllegalStateException] 若找不到指定的键
     */
    operator fun <T> get(keysSplitByDoc: String): T {
        return getByKeys(keysSplitByDoc.splitToSequence(".").iterator(), rootMapObject)
    }

    /**
     * [get] 的安全版本，若找不到指定的键，则返回 [defaultValueBlock] 的返回值。
     *
     * @param [keysSplitByDoc] 由`.`分隔的键
     * @param [defaultValueBlock] 若找不到指定的键，则返回该函数的返回值
     * @see [get]
     */
    inline fun <T> getOrElse(keysSplitByDoc: String, defaultValueBlock: (throwable: Throwable) -> T): T {
        return runCatching {
            this.get<T>(keysSplitByDoc)
        }.getOrElse {
            defaultValueBlock(it)
        }
    }

    /**
     * [get] 的安全版本，若找不到指定的键，则返回 `null`。
     *
     * @param [keysSplitByDoc] 由`.`分隔的键
     * @see [get]
     */
    fun <T> getOrNull(keysSplitByDoc: String): T? {
        return getOrElse(keysSplitByDoc) { null }
    }

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

    fun save(): Result<Unit> = runCatching {
        yaml.dump(rootMapObject, FileWriter(path.absolutePathString()))
    }
}
