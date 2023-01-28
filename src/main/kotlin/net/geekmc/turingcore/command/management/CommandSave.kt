package net.geekmc.turingcore.command.management

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.framework.AutoRegister
import net.geekmc.turingcore.instance.InstanceService
import net.geekmc.turingcore.util.color.message
import world.cepi.kstom.command.kommand.Kommand
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@AutoRegister
@OptIn(ExperimentalTime::class)
object CommandSave : Kommand({

    opSyntax {
        val world = InstanceService.getInstance(InstanceService.MAIN_INSTANCE_ID).apply {
            @Suppress("UnstableApiUsage")
            saveInstance()
        }
        sender.message("&g成功保存全局数据.")
        val time = measureTime {
            world.saveChunksToStorage()
        }.inWholeMilliseconds
        sender.message("&g成功保存 &y${world.chunks.size} &g个区块,耗时 &y$time &ms")
    }
}, name = "save")