package net.geekmc.turingcore.util.extender

import net.geekmc.turingcore.data.getData
import net.geekmc.turingcore.player.essentialdata.EssentialPlayerData
import net.minestom.server.command.CommandSender
import net.minestom.server.command.ConsoleSender
import net.minestom.server.command.ServerSender
import net.minestom.server.entity.Player
import world.cepi.kstom.command.kommand.Kondition

fun CommandSender.isOp(): Boolean {
    return when (this) {
        is Player -> this.getData<EssentialPlayerData>().isOp
        is ConsoleSender -> true
        is ServerSender -> true
        else -> false
    }
}

fun <T : Kondition<T>> Kondition<T>.onlyOp() =
    condition {
        sender.isOp()
    }