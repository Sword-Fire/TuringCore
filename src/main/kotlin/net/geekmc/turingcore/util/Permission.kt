package net.geekmc.turingcore.util

import net.geekmc.turingcore.player.data
import net.minestom.server.command.CommandSender
import net.minestom.server.command.ConsoleSender
import net.minestom.server.entity.Player

fun CommandSender.isOp(): Boolean {
    // TODO remove this
    return true
    return when (this) {
        is Player -> data["op"] ?: false
        is ConsoleSender -> true
        else -> false
    }
}