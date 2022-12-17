package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.util.color.message
import net.geekmc.turingcore.util.info
import net.geekmc.turingcore.util.replaceWithOrder
import net.minestom.server.command.CommandSender

class TypeText : Type {

    constructor()

    constructor(text: String) {
        if (text.isNotEmpty() && text.isNotBlank()) {
            texts = mutableListOf(text)
        }
    }

    var texts: MutableList<String>? = null

    override fun init(source: Map<String, Any>): Boolean {
        @Suppress("UNCHECKED_CAST")
        texts = source["texts"] as? MutableList<String> ?: return false
        return true
    }

    override fun send(sender: CommandSender, vararg args: Any) {
        texts?.forEach { sender.message(it.replaceWithOrder(*args)) }
    }
}