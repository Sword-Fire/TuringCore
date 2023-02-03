package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.library.color.message
import net.minestom.server.command.CommandSender

fun CommandSender.sendLang(node: String, vararg args: Any) {
    val type = LanguageService.messageMap[node]
    if (type == null) {
        message(node)
        return
    }
    type.send(this, *args)
}