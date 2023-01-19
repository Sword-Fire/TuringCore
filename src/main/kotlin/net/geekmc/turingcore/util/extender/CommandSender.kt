package net.geekmc.turingcore.util.extender

import net.minestom.server.command.CommandSender
import net.minestom.server.command.ConsoleSender
import net.minestom.server.command.ServerSender
import net.minestom.server.entity.Player

val CommandSender.displayName
    get() = when (this) {
        is Player -> "Player: [${this.uuid}, ${this.displayName}]"
        is ConsoleSender -> "Console"
        is ServerSender -> "Server"
        else -> "Unknown, $this"
    }