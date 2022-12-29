package net.geekmc.turingcore.util.color

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender

fun CommandSender.message(vararg message: Any) {
    message.forEach { sendMessage(it.toString().toComponent()) }
}

/**
 * 将String对象转换为Component对象，其中 &颜色符号 会被转化为颜色代码， && 会被替换为&以使得使用者能打出&符号，&$ 会被替换为§以使得使用者能打出§符号。
 */
fun String.toComponent(): Component {
    var str = this
    str = str.replace("&&", "{§0}")
    str = str.replace("&$", "{§1}")
    ColorUtil.colorMap.forEach {
        str = str.replace(it.key, it.value)
    }
    str = str.replace("{§0}", "&")
    str = str.replace("{§1}", "§")
    return ColorUtil.miniMessage.deserialize(str)
}