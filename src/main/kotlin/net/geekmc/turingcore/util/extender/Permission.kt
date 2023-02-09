package net.geekmc.turingcore.util.extender

import net.geekmc.turingcore.library.data.player.getData
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.player.essentialdata.EssentialPlayerData
import net.geekmc.turingcore.util.lang.Lang
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.CommandSender
import net.minestom.server.command.ConsoleSender
import net.minestom.server.command.ServerSender
import net.minestom.server.entity.Player
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kondition

fun CommandSender.isOp(): Boolean {
    return when (this) {
        is Player -> this.getData<EssentialPlayerData>().isOp
        is ConsoleSender -> true
        is ServerSender -> true
        else -> false
    }
}

val di = turingCoreDi
val lang: Lang by di.instance()

fun <T : Kondition<T>> Kondition<T>.onlyOp() =
    condition {
        val isOp = sender.isOp()
        if (!isOp) {
            sender.sendLang(lang, "global.cmd.onlyOp")
        }
        isOp
    }

fun <T : Kondition<T>> Kondition<T>.onlyPlayer() =
    condition {
        val isPlayer = sender is Player
        if (!isPlayer) {
            sender.sendLang(lang, "global.cmd.onlyPlayer")
        }
        isPlayer
    }

fun <T : Kondition<T>> Kondition<T>.onlyConsole() =
    condition {
        val isConsole = sender is ConsoleSender
        if (!isConsole) {
            sender.sendLang(lang, "global.cmd.onlyConsole")
        }
        isConsole
    }