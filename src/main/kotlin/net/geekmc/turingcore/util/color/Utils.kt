package net.geekmc.turingcore.util.color

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender

fun CommandSender.message(vararg message: Any) {
    message.forEach { sendMessage(it.toString().toComponent()) }
}

fun String.toComponent(): Component {
    var str = this
    str = str.replace("&&", "{§}")
    ColorUtil.colorMap.forEach {
        str = str.replace(it.key, it.value)
    }
    str = str.replace("{§}", "&")
    return ColorUtil.miniMessage.deserialize(str)
}