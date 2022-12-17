package net.geekmc.turingcore.command.basic

import net.geekmc.turingcore.command.args
import net.geekmc.turingcore.command.findPlayers
import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.util.color.message
import net.geekmc.turingcore.util.foldToString
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.Player
import world.cepi.kstom.command.kommand.Kommand

object CommandKill : Kommand({

    val targetArg = ArgumentEntity("target").onlyPlayers(true)

    opSyntax {
        sender.sendLang("message-command-wrong-usage")
    }

    opSyntax(targetArg) {
        val players = (!targetArg).findPlayers(sender)
        if (players.isEmpty()) {
            sender.sendLang("message-command-player-cannot-found", args.getRaw(targetArg))
            return@opSyntax
        }
        players.forEach { it.kill() }
        sender.sendLang("message-command-kill-succ", players.foldToString())
    }
}, name = "kill")