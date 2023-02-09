package net.geekmc.turingcore.command.basic

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.lang.ExtensionLang
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandTeleport : Kommand({

    val vecArg = ArgumentRelativeVec3("vec")
    val targetArg = ArgumentEntity("target")

    val lang by turingCoreDi.instance<ExtensionLang>()

    notPlayerAction = {
        it.sendLang(lang, "global-message-command-player-only")
    }

    opSyntax {
        sender.sendLang(lang, "global-message-command-wrong-usage")
    }

    opSyntax(vecArg) {
        if (sender !is Player) {
            sender.sendLang(lang, "global-message-command-player-only")
            return@opSyntax
        }
        val pos = Pos.fromPoint((!vecArg).from(player))
        player.teleport(pos)
        sender.sendLang(lang, "message-command-teleport-succ", player.username, pos.toString())
    }

    opSyntax(vecArg, targetArg) {
        val entities = (!targetArg).find(sender)
        val pos = Pos.fromPoint((!vecArg).from(player))
        entities.forEach {
            it.teleport(pos)
        }
        val name = entities.joinToString(", ") {
            when (it) {
                is Player -> it.username
                else -> it.entityType.toString()
            }
        }
        sender.sendLang(lang, "message-command-teleport-succ", name, pos.toString())
    }
}, name = "teleport", aliases = arrayOf("tp"))