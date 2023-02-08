package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.library.color.message
import net.minestom.server.command.CommandSender

fun CommandSender.sendLang(lang: Lang, node: String, vararg args: Any) {
    val msg = lang[node]
    if (msg != null) {
        msg.send(this, *args)
    } else {
        message(node)
    }
}