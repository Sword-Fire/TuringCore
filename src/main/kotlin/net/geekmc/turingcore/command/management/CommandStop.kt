package net.geekmc.turingcore.command.management

import net.geekmc.turingcore.library.color.toComponent
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.help
import net.geekmc.turingcore.util.extender.onlyOp
import net.geekmc.turingcore.util.lang.Lang
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.arguments.ArgumentString
import net.minestom.server.timer.TaskSchedule
import org.kodein.di.instance
import world.cepi.kstom.Manager
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandStop : Kommand({

    val reasonArg = ArgumentString("reason").setDefaultValue("&r服务器重启.")
    val lang: Lang by turingCoreDi.instance()

    help {
        sender.sendLang(lang, "cmd.stop.help")
    }

    syntax {
        sender.sendLang(lang, "cmd.stop.succ")
        val reason = !reasonArg
        for (p in Manager.connection.onlinePlayers) {
            p.kick(reason.toComponent())
        }
        // 必须延迟至少 1 tick 后关服，因为 PlayerConnection.disconnect() 方法有 1 tick 延迟。
        Manager.scheduler.buildTask { MinecraftServer.stopCleanly() }.delay(TaskSchedule.tick(10)).schedule()
    }.onlyOp()

}, name = "stop")