package net.geekmc.turingcore.command

import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.util.extender.isOp
import net.geekmc.turingcore.util.lang.GlobalLang
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.Player
import net.minestom.server.utils.entity.EntityFinder
import org.jetbrains.annotations.Contract
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kommand

val Kommand.SyntaxContext.args
    get() = this.context

@Contract(pure = true)
inline fun Kommand.opSyntax(
    vararg arguments: Argument<*> = arrayOf(),
    crossinline executor: Kommand.SyntaxContext.() -> Unit
) = syntax(*arguments, executor = {
    val globalLang by turingCoreDi.instance<GlobalLang>()
    if (!sender.isOp()) {
        sender.sendLang(globalLang, "global.cmd.opOnly")
        return@syntax
    }
    executor()
})

/**
 * 查找在 [sender] 所在世界内的所有匹配的玩家。
 */
fun EntityFinder.findPlayers(sender: CommandSender): List<Player> {
    return find(sender).filterIsInstance<Player>()
}

fun ArgumentEntity.setDefaultValueToSelf(): ArgumentEntity {
    setDefaultValue(EntityFinder().setTargetSelector(EntityFinder.TargetSelector.SELF))
    return this
}