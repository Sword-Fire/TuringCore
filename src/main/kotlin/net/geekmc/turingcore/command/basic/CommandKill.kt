package net.geekmc.turingcore.command.basic

import net.geekmc.turingcore.command.args
import net.geekmc.turingcore.command.findPlayers
import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.library.di.TuringCoreDIAware
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.joinToString
import net.geekmc.turingcore.util.lang.Lang
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandKill : Kommand({

    val di = turingCoreDi
    val lang: Lang by di.instance()

    help {
        sender.sendLang(lang, "cmd.kill.help")
    }

    val targetArg = ArgumentEntity("target").onlyPlayers(true)

    opSyntax(targetArg) {
        val players = (!targetArg).findPlayers(sender)
        if (players.isEmpty()) {
            sender.sendLang(lang, "global.cmd.playerNotFound", args.getRaw(targetArg))
            return@opSyntax
        }
        players.forEach { it.kill() }
        sender.sendLang(lang, "cmd.kill.succ", players.joinToString())
    }
}, name = "kill"), TuringCoreDIAware