package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.util.color.toComponent
import net.geekmc.turingcore.util.replaceWithOrder
import net.minestom.server.command.CommandSender

class TypeActionBar : Type {

    private var text: String? = null

    override fun init(source: Map<String, Any>): Boolean {
        text = source["text"]?.toString() ?: return false
        return true
    }

    override fun send(sender: CommandSender, vararg args: Any) {
        sender.sendActionBar(text?.replaceWithOrder(args)?.toComponent() ?: return)
    }
}