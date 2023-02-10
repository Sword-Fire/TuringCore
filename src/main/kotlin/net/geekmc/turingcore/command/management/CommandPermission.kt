package net.geekmc.turingcore.command.management

import net.geekmc.turingcore.command.args
import net.geekmc.turingcore.command.findPlayers
import net.geekmc.turingcore.command.setDefaultValueToSelf
import net.geekmc.turingcore.library.color.message
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.help
import net.geekmc.turingcore.util.extender.lang
import net.geekmc.turingcore.util.extender.onlyOp
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.ArgumentLiteral
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.Player
import world.cepi.kstom.command.kommand.Kommand
import world.cepi.kstom.util.addPermission

@AutoRegister
object CommandPermission : Kommand({

    val addArg = ArgumentLiteral("add")
    val removeArg = ArgumentWord("remove").from("remove", "rem")
    val listArg = ArgumentLiteral("list")
    val targetArg = ArgumentEntity("target").onlyPlayers(true).setDefaultValueToSelf()
    val permArg = ArgumentWord("perm")

    help {
        sender.sendLang(lang, "cmd.perm.help")
    }

    syntax(listArg, targetArg) {
        val target = (!targetArg).findFirstPlayer(sender)
        if (target == null) {
            if (sender !is Player && args.getRaw(targetArg).isEmpty()) {
                sender.sendLang(lang, "cmd.perm.noTarget")
            } else {
                sender.sendLang(lang, "global.cmd.playerNotFound", raw(targetArg))
            }
            return@syntax
        }
        sender.sendLang(lang,"cmd.perm.list", target.username)
        target.allPermissions.forEach {
            sender.message(" &y- ${it.permissionName}")
        }
    }.onlyOp()

    syntax(addArg, permArg, targetArg) {
        val players = (!targetArg).findPlayers(sender)
        if (players.isEmpty()) {
            // 不能使用 !target == target.defaultValue
            if (sender !is Player && args.getRaw(targetArg).isEmpty()) {
                sender.sendLang(lang, "cmd.perm.noTarget")
            } else {
                sender.sendLang(lang, "global.cmd.playerNotFound", raw(targetArg))
            }
            return@syntax
        }
        players.forEach {
            it.addPermission(!permArg)
        }
        sender.sendLang(lang, "cmd.perm.add", !permArg, players.map { it.username })
    }.onlyOp()

    syntax(removeArg, permArg, targetArg) {
        val players = (!targetArg).findPlayers(sender)
        if (players.isEmpty()) {
            if (sender !is Player && args.getRaw(targetArg).isEmpty()) {
                sender.sendLang(lang, "cmd.perm.noTarget")
            } else {
                sender.sendLang(lang, "global.cmd.playerNotFound", raw(targetArg))
            }
            return@syntax
        }
        players.forEach {
            it.removePermission(!permArg)
        }
        sender.sendLang(lang, "cmd.perm.remove", !permArg, players.map { it.username })
    }.onlyOp()

}, name = "perm", aliases = arrayOf("p"))