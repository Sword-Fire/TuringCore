package net.geekmc.turingcore.ktx.command.kommand

import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.entity.Player

/**
 * From [KStom](https://github.com/Project-Cepi/KStom).
 */
class KSyntax(
    vararg val arguments: Argument<*>,
    override val conditions: MutableList<Kommand.ConditionContext.() -> Boolean> = mutableListOf(),
    override val kommandReference: Kommand
) : Kondition<KSyntax>() {
    override val t: KSyntax
        get() = this

    operator fun invoke(executor: Kommand.SyntaxContext.() -> Unit) {
        if (arguments.isEmpty()) {
            kommandReference.command.setDefaultExecutor { sender, context ->

                if (!conditionPasses(Kommand.ConditionContext(sender, sender as? Player, context.input))) return@setDefaultExecutor

                executor(Kommand.SyntaxContext(sender, context))
            }

            return
        }

        kommandReference.command.addConditionalSyntax(
            { sender, string -> conditionPasses(Kommand.ConditionContext(sender, sender as? Player, string ?: "")) },
            { sender, context -> executor(Kommand.SyntaxContext(sender, context)) },
            *arguments
        )

        return
    }
}