package net.geekmc.turingcore.command.management

import net.geekmc.turingcore.command.args
import net.geekmc.turingcore.command.findPlayers
import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.command.setDefaultValueToSelf
import net.geekmc.turingcore.util.color.message
import net.minestom.server.command.builder.arguments.ArgumentLiteral
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.Player
import world.cepi.kstom.command.kommand.Kommand
import world.cepi.kstom.util.addPermission

object CommandPermission : Kommand({

    val addArg = ArgumentLiteral("add")
    val removeArg = ArgumentWord("remove").from("remove", "rem")
    val listArg = ArgumentLiteral("list")
    val targetArg = ArgumentEntity("target").onlyPlayers(true).setDefaultValueToSelf()
    val permArg = ArgumentWord("perm")

    playerCallbackFailMessage = {
        it.message("&r只有玩家能使用这个命令.")
    }

    opSyntax {
        sender.message("&r命令用法不正确: /${context.input}")
        sender.message("&r输入 /perm help 来了解用法.")
    }

    opSyntax(addArg, permArg, targetArg) {
        val players = (!targetArg).findPlayers(sender)
        if (players.isEmpty()) {
            // 不能使用 !target == target.defaultValue
            if (sender !is Player && args.getRaw(targetArg).isEmpty()) {
                sender.message("&r非玩家使用该命令时不能省略参数 target.")
            } else {
                sender.message("&r找不到玩家: ${args.getRaw(targetArg)}")
            }
            return@opSyntax
        }
        players.forEach {
            it.addPermission(!permArg)
        }
        sender.message("&g已将权限 ${!permArg} 赋与 ${players.map { it.username }}")
    }

    opSyntax(removeArg, permArg, targetArg) {
        val players = (!targetArg).findPlayers(sender)
        if (players.isEmpty()) {
            if (sender !is Player && args.getRaw(targetArg).isEmpty()) {
                sender.message("&r非玩家使用该命令时不能省略参数 target.")
            } else {
                sender.message("&r找不到玩家: ${args.getRaw(targetArg)}")
            }
            return@opSyntax
        }
        players.forEach {
            it.removePermission(!permArg)
        }
        sender.message("&y已将权限 ${!permArg} 移除自 ${players.map { it.username }}")
    }

    opSyntax(listArg, targetArg) {
        val target = (!targetArg).findFirstPlayer(sender)
        if (target == null) {
            if (sender !is Player && args.getRaw(targetArg).isEmpty()) {
                sender.message("&r非玩家使用该命令时不能省略参数 target.")
            } else {
                sender.message("&r找不到玩家: ${args.getRaw(targetArg)}")
            }
            return@opSyntax
        }
        sender.message("&g玩家 ${target.username} 拥有以下权限:")
        target.allPermissions.forEach {
            sender.message(" &y- ${it.permissionName}")
        }
    }
}, name = "perm", aliases = arrayOf("p"))