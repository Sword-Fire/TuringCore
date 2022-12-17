package net.geekmc.turingcore.command.management

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.util.color.message
import net.minestom.server.MinecraftServer
import world.cepi.kstom.command.kommand.Kommand

object CommandStop : Kommand({

    opSyntax {
        sender.message("&r正在关闭服务器.")
        MinecraftServer.stopCleanly()
    }
}, name = "stop")