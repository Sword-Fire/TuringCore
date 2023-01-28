package net.geekmc.turingcore.command.management

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.framework.AutoRegister
import net.geekmc.turingcore.util.color.message
import net.geekmc.turingcore.util.color.toComponent
import net.minestom.server.MinecraftServer
import net.minestom.server.timer.TaskSchedule
import world.cepi.kstom.Manager
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandStop : Kommand({

    opSyntax {
        sender.message("&rStopping the server.")
        for (p in Manager.connection.onlinePlayers) {
            p.kick("&rServer restart".toComponent())
            println("kicked ${p.username}")
        }
        // 必须延迟至少 1 tick 后关服，因为 PlayerConnection.disconnect() 方法有 1 tick 延迟。
        Manager.scheduler.buildTask { MinecraftServer.stopCleanly() }.delay(TaskSchedule.tick(10)).schedule()
    }
}, name = "stop")