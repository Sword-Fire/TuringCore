package net.geekmc.turingcore.command

import net.geekmc.turingcore.util.isOp
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.Player
import net.minestom.server.utils.entity.EntityFinder
import org.jetbrains.annotations.Contract
import world.cepi.kstom.command.kommand.Kommand

val Kommand.SyntaxContext.args
    get() = this.context

@Contract(pure = true)
fun Kommand.opSyntax(
    vararg arguments: Argument<*> = arrayOf(),
    executor: Kommand.SyntaxContext.() -> Unit
) = syntax(*arguments, executor = {
    if (!sender.isOp()) {
        sender.sendLang("message-command-operator-only")
        return@syntax
    }
    executor()
})

fun EntityFinder.findPlayers(sender: CommandSender): List<Player> {
    return find(sender).filterIsInstance<Player>()
}

fun ArgumentEntity.setDefaultValueToSelf(): ArgumentEntity {
    setDefaultValue(EntityFinder().setTargetSelector(EntityFinder.TargetSelector.SELF))
    return this
}