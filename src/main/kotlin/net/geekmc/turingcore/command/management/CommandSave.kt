package net.geekmc.turingcore.command.management

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.instance.InstanceService
import net.geekmc.turingcore.util.color.message
import net.geekmc.turingcore.util.timeOf
import world.cepi.kstom.command.kommand.Kommand

object CommandSave : Kommand({

    opSyntax {
        val world = InstanceService.getInstance(InstanceService.MAIN_INSTANCE_ID).apply {
            @Suppress("UnstableApiUsage")
            saveInstance()
        }
        sender.message("&g成功保存全局数据.")
        val time = timeOf {
            world.saveChunksToStorage()
        }
        sender.message("&g成功保存 &y${world.chunks.size} &g个区块,耗时 &y$time &ms")
    }
}, name = "save")