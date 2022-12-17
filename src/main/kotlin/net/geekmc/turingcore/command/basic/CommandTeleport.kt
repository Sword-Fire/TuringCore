package net.geekmc.turingcore.command.basic

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.util.foldToString
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import world.cepi.kstom.command.kommand.Kommand

object CommandTeleport : Kommand({

    val vecArg = ArgumentRelativeVec3("vec")
    val targetArg = ArgumentEntity("target")

    playerCallbackFailMessage = {
        it.sendLang("message-command-player-only")
    }

    opSyntax {
        sender.sendLang("message-command-wrong-usage")
    }

    opSyntax(vecArg) {
        if (sender !is Player) {
            sender.sendLang("message-command-player-only")
            return@opSyntax
        }
        val pos = Pos.fromPoint((!vecArg).from(player))
        player.teleport(pos)
        sender.sendLang("message-command-teleport-succ", player.username, pos.toString())
    }

    opSyntax(vecArg, targetArg) {
        val entities = (!targetArg).find(sender)
        val pos = Pos.fromPoint((!vecArg).from(player))
        entities.forEach {
            it.teleport(pos)
        }
        val name = entities.foldToString(", ") {
            when (it) {
                is Player -> it.username
                else -> it.entityType.toString()
            }
        }
        sender.sendLang("message-command-teleport-succ", name, pos.toString())
    }
}, name = "teleport", aliases = arrayOf("tp"))