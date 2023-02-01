package net.geekmc.turingcore.library.color

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender

fun CommandSender.message(vararg message: Any) {
    message.forEach { sendMessage(it.toString().toComponent()) }
}

/**
 * 将 String 对象转换为 Component 对象，其中 '&颜色符号' 会被转换为颜色代码(e.g. '&r' -> 红色颜色代码)，
 * '&&' 会被转换为 '&' ( '&&' -> '&' )， '&$' 会被转换为 '§' ( '&$' -> '§' )。
 */
fun String.toComponent(): Component {
    var str = this
    str = str.replace("&&", "{__AND_SIGN__}")
    str = str.replace("&$", "{__SECTION_SIGN__}")
    ColorService.colorMap.forEach {
        str = str.replace(it.key, it.value)
    }
    str = str.replace("{__AND_SIGN__}", "&")
    str = str.replace("{__SECTION_SIGN__}", "§")
    return ColorService.miniMessage.deserialize(str)
}