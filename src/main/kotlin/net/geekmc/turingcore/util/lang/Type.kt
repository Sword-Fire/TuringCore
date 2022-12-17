package net.geekmc.turingcore.util.lang

import net.minestom.server.command.CommandSender

/**
 * t1: "example message"
 * t2:
 *   - "example messages"
 * t3:
 * - type: actionbar
 *   text: "example actionbar message"
 * t4:
 * - type: title
 *   title: "example title message"
 *   subtitle: "example subtitle message"
 *   fadein: 0
 *   stay: 1.0
 *   fadeout: 0
 */
interface Type {

    fun init(source: Map<String, Any>): Boolean

    fun send(sender: CommandSender, vararg args: Any)
}