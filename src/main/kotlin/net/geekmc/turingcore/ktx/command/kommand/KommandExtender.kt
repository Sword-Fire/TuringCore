package net.geekmc.turingcore.ktx.command.kommand

//import net.geekmc.turingcore.color.send
//import net.geekmc.turingcore.permission.isOp
import net.minestom.server.command.ConsoleSender
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.entity.Player

val Kommand.SyntaxContext.args
    get() = this.context

val Kommand.OP: FormatLimits
    get() = FormatLimits.OP
val Kommand.PLAYER: FormatLimits
    get() = FormatLimits.PLAYERS
val Kommand.CONSOLE: FormatLimits
    get() = FormatLimits.CONSOLE

// 0 arg
fun Kommand.format(
    vararg arguments: Argument<*> = arrayOf(),
    executor: Kommand.SyntaxContext.() -> Unit
) = this@format.format(
    FormatLimits.NONE,
    FormatLimits.NONE,
    FormatLimits.NONE,
    FormatLimits.NONE,
    FormatLimits.NONE,
    *arguments,
    executor = executor
)

// 1 arg
fun Kommand.format(
    lim1: FormatLimits,
    vararg arguments: Argument<*> = arrayOf(),
    executor: Kommand.SyntaxContext.() -> Unit
) = this@format.format(
    lim1,
    FormatLimits.NONE,
    FormatLimits.NONE,
    FormatLimits.NONE,
    FormatLimits.NONE,
    *arguments,
    executor = executor
)

// 2 arg
fun Kommand.format(
    lim1: FormatLimits,
    lim2: FormatLimits,
    vararg arguments: Argument<*> = arrayOf(),
    executor: Kommand.SyntaxContext.() -> Unit
) = this@format.format(
    lim1,
    lim2,
    FormatLimits.NONE,
    FormatLimits.NONE,
    FormatLimits.NONE,
    *arguments,
    executor = executor
)

// 3 arg
fun Kommand.format(
    lim1: FormatLimits,
    lim2: FormatLimits,
    lim3: FormatLimits,
    vararg arguments: Argument<*> = arrayOf(),
    executor: Kommand.SyntaxContext.() -> Unit
) = this@format.format(lim1, lim2, lim3, FormatLimits.NONE, FormatLimits.NONE, *arguments, executor = executor)

// 4 arg
fun Kommand.format(
    lim1: FormatLimits,
    lim2: FormatLimits,
    lim3: FormatLimits,
    lim4: FormatLimits,
    vararg arguments: Argument<*> = arrayOf(),
    executor: Kommand.SyntaxContext.() -> Unit
) = this@format.format(lim1, lim2, lim3, lim4, FormatLimits.NONE, *arguments, executor = executor)

// 5 arg
fun Kommand.format(
    lim1: FormatLimits,
    lim2: FormatLimits,
    lim3: FormatLimits,
    lim4: FormatLimits,
    lim5: FormatLimits,
    vararg arguments: Argument<*> = arrayOf(),
    executor: Kommand.SyntaxContext.() -> Unit
) = syntax(*arguments, executor = {

    val limits = setOf(lim1, lim2, lim3, lim4, lim5)

//    if (limits.contains(FormatLimits.OP) && !sender.isOp()) {
//        sender.send("&r只有管理员能使用这个命令!")
//        return@syntax
//    }
    if (limits.contains(PLAYER) && sender !is Player) {
//        sender.send("&r只有玩家能使用这个命令!")
        sender.sendMessage("&r只有玩家能使用这个命令!")
        return@syntax
    }
    if (limits.contains(FormatLimits.CONSOLE) && sender !is ConsoleSender) {
//        sender.send("&r只有控制台能使用这个命令!")
        sender.sendMessage("&r只有控制台能使用这个命令!")
        return@syntax
    }

    executor()
})