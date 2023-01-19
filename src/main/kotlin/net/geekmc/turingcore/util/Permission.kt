package net.geekmc.turingcore.util

import net.geekmc.turingcore.player.data
import net.minestom.server.command.CommandSender
import net.minestom.server.command.ConsoleSender
import net.minestom.server.entity.Player
import world.cepi.kstom.command.kommand.Kondition

fun CommandSender.isOp(): Boolean {
    // TODO remove this
    return true
    return when (this) {
        is Player -> data["op"] ?: false
        is ConsoleSender -> true
        else -> false
    }
}

fun <T : Kondition<T>> Kondition<T>.onlyOp() =
    condition {
        sender.isOp()
    }