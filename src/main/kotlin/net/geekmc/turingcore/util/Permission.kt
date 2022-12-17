package net.geekmc.turingcore.util

import net.geekmc.turingcore.service.impl.player.data
import net.minestom.server.command.CommandSender
import net.minestom.server.command.ConsoleSender
import net.minestom.server.entity.Player

fun CommandSender.isOp(): Boolean {
    return when (this) {
        is Player -> data["op"] ?: false
        is ConsoleSender -> true
        else -> false
    }
}