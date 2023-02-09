package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.library.color.message
import net.minestom.server.command.CommandSender

fun CommandSender.sendLang(lang: Lang, node: String, vararg args: Any) {
    val msg = lang[node]
    if (msg != null) {
        msg.send(this, *args)
    } else {
        message("&r错误: 找不到语言节点 &w$node")
    }
}