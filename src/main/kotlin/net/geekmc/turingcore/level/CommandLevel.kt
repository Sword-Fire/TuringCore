package net.geekmc.turingcore.level

import net.geekmc.turingcore.library.di.TuringCoreDIAware
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.help
import net.geekmc.turingcore.util.lang.Lang
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.number.ArgumentLong
import org.kodein.di.instance
import world.cepi.kstom.command.arguments.defaultValue
import world.cepi.kstom.command.arguments.suggestAllPlayers
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandLevel: Kommand({
    val di = turingCoreDi
    val lang: Lang by di.instance()

    val actionArg = ArgumentWord("action").from("see", "set", "add", "remove")
    val playerArg = ArgumentWord("player").suggestAllPlayers().defaultValue("\$self")
    val amountArg = ArgumentLong("level")

    help {
        sender.sendMessage("错误的命令: ${context.input}")
        sender.sendMessage("Usage: level help")
        sender.sendMessage("Usage: level level see|set|add|remove [player] <level>")
        sender.sendMessage("Usage: level exp see|set|add|remove [player] <level>")
    }

    subcommand("level") {
        syntax(actionArg, playerArg, amountArg) {

        }
    }

    subcommand("exp") {
        syntax(actionArg, playerArg, amountArg) {

        }
    }

}, name = "level"), TuringCoreDIAware