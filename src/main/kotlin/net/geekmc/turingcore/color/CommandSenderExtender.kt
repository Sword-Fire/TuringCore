package net.geekmc.turingcore.color

import net.minestom.server.command.CommandSender

/**
 * Send message with [ColorUtil] support
 *
 * @see ColorUtil
 */
fun CommandSender.send(message: String) =
    sendMessage(message.toComponent())