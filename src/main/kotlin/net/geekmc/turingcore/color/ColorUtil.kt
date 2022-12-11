package net.geekmc.turingcore.color

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.*

/**
 * Cast [String] to [Component]
 *
 * @see [ColorUtil.castStringToComponent]
 */
fun String.toComponent(): Component {
    return ColorUtil.castStringToComponent(this)
}

/**
 * 用于把String转换成Component的工具，内部使用MiniMessage实现。
 *
 * 支持自定义简写，如&r -> <red>。
 */
object ColorUtil {

    private val miniMessage = MiniMessage.miniMessage()

    /**
     * Store customs colors, and sort them by length desc
     *
     * e.g., &a -> <#111111>
     */
    private val colorMap = TreeMap<String, String> { x, y ->
        if (x.length != y.length) return@TreeMap -x.length.compareTo(y.length) // *-1
        return@TreeMap x.compareTo(y)
    }

//    private const val CONFIG = "CustomColors.yml"

    /**
     * Load customs colors from yaml
     *
     * @see [colorMap]
     */
    private fun loadCustomColors() {
        // TODO: loadCustomColors impl
//        TuringCore.INSTANCE.saveResource("CustomColors.yml")
//        val data = YamlData(TuringCore.INSTANCE.resolvePath(CONFIG))
//
//        val colors: List<String> = data.get("colors", listOf())
//        for (str in colors) {
//            val split = str.split("@")
//            if (split.size != 2) {
//                TuringCore.INSTANCE.logger.warn("无法解析颜色格式: $str")
//                continue
//            }
//            colorMap[split[0]] = "<${split[1]}>"
//        }
    }

    /**
     * Initialize
     */
    fun init() {
        loadCustomColors()
    }

    /**
     * Cast [String] to [Component]
     *
     * "&&" in [String] will be replaced with "&"
     */
    fun castStringToComponent(string: String): Component {
        // TODO: Dirty code, may stream?
        return miniMessage.deserialize(string.run {
            var retval = replace("&&", "{§}")
            colorMap.forEach { (k, v) ->
                retval = retval.replace(k, v)
            }
            retval = retval.replace("{§}", "&")
            retval
        })
    }
}