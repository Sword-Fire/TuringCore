package net.geekmc.turingcore.util.lang

import net.minestom.server.command.CommandSender

/**
 * ```
 * msg-example-1: "chat message"
 * msg-example-2:
 *   type: "title"
 *   main: "message in main title"
 *   sub: "message in sub title"
 *   // 单位为tick。如果不填，会自动根据消息长度计算时间。不会低于1.5s。
 *   fadein: 20
 *   duration: 100
 *   fadeout: 20
 * msg-example-3:
 *   type: "actionbar"
 *   msg: "actionbar message"
 *   // 单位为tick。如果不填，会自动根据消息长度计算时间。不会低于1.5s。
 *   duration: 100
 * msg-example-4: null
 * msg-example-5:
 *   type: "multi"
 *   msg: "chat message1\n
 *         chat message2 "
 *  ```
 */
sealed interface Message {
    fun send(sender: CommandSender, vararg args: Any)
}