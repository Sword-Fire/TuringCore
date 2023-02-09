package net.geekmc.turingcore.command.basic

import net.geekmc.turingcore.command.args
import net.geekmc.turingcore.command.findPlayers
import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.joinToString
import net.geekmc.turingcore.util.lang.ExtensionLang
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandKill : Kommand({

    val targetArg = ArgumentEntity("target").onlyPlayers(true)

    val lang by turingCoreDi.instance<ExtensionLang>()

    opSyntax {
        sender.sendLang(lang, "global-message-command-wrong-usage")
    }

    opSyntax(targetArg) {
        val players = (!targetArg).findPlayers(sender)
        if (players.isEmpty()) {
            sender.sendLang(lang, "global-message-command-player-cannot-found", args.getRaw(targetArg))
            return@opSyntax
        }
        players.forEach { it.kill() }
        sender.sendLang(lang, "global-message-command-kill-succ", players.joinToString())
    }
}, name = "kill")