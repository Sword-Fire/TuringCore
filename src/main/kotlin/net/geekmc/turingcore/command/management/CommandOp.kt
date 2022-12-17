package net.geekmc.turingcore.command.management

import net.geekmc.turingcore.command.args
import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.service.impl.player.data
import net.geekmc.turingcore.util.color.message
import net.minestom.server.command.builder.arguments.ArgumentBoolean
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import world.cepi.kstom.command.arguments.defaultValue
import world.cepi.kstom.command.kommand.Kommand

object CommandOp : Kommand({

    val targetArg = ArgumentEntity("target")
        .onlyPlayers(true)
        .singleEntity(true)
    val booleanArg = ArgumentBoolean("boolean").defaultValue(true)

    opSyntax {
        sender.message("&r命令用法不正确: /${context.input}")
        sender.message("&r正确用法: /op <Player> [Boolean]")
    }

    opSyntax(targetArg, booleanArg) {
        val player = (!targetArg).findFirstPlayer(sender) ?: kotlin.run {
            sender.message("&r找不到玩家: ${args.getRaw(targetArg)}")
            return@opSyntax
        }
        player.data["op"] = !booleanArg
        sender.message("&g已将玩家玩家 &y${player.username} &g的管理员权限设置为 &r${!booleanArg}")
    }
}, name = "operator", aliases = arrayOf("op"))