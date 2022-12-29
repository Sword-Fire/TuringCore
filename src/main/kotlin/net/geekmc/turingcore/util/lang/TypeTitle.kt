package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.util.color.toComponent
import net.kyori.adventure.title.Title
import net.minestom.server.command.CommandSender
import java.time.Duration

class TypeTitle : Type {

    private var title: String? = null
    private var subtitle: String? = null
    private var fadein = 0L
    private var stay = 1000L
    private var fadeout = 0L

    override fun init(source: Map<String, Any>): Boolean {
        title = source["title"]?.toString() ?: return false
        subtitle = source["subtitle"]?.toString() ?: return false
        // 以秒类型作为输入。
        fadein = source["fadein"]?.toString()?.toDoubleOrNull()?.times(1000)?.toLong() ?: return false
        stay = source["stay"]?.toString()?.toDoubleOrNull()?.times(1000)?.toLong() ?: return false
        fadeout = source["fadeout"]?.toString()?.toDoubleOrNull()?.times(1000)?.toLong() ?: return false
        return true
    }

    override fun send(sender: CommandSender, vararg args: Any) {
        val times = Title.Times.times(Duration.ofMillis(fadein), Duration.ofMillis(stay), Duration.ofMillis(fadeout))
        val title = Title.title(title?.toComponent() ?: return, subtitle?.toComponent() ?: return, times)
        sender.showTitle(title)
    }
}