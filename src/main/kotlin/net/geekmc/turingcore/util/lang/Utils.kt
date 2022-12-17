package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.util.color.message
import net.geekmc.turingcore.util.info
import net.geekmc.turingcore.util.replaceWithOrder
import net.minestom.server.command.CommandSender

fun getLangText(node: String, vararg args: Any): String {
    val text = LanguageUtil.messageMap[node] as? TypeText ?: return node
    return text.texts?.get(0)?.replaceWithOrder(args) ?: node
}

fun getLangTextList(node: String, vararg args: Any): List<String> {
    val type = LanguageUtil.messageMap[node] as? TypeText ?: return mutableListOf(node)
    val texts = type.texts ?: return mutableListOf(node)
    return texts.map { it.replaceWithOrder(args) }
}

fun CommandSender.sendLang(node: String, vararg args: Any) {
    val type = LanguageUtil.messageMap[node]
    if (type == null) {
        message(node)
        return
    }
    type.send(this, *args)
}