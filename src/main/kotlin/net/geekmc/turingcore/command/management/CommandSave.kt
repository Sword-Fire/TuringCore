package net.geekmc.turingcore.command.management

import net.geekmc.turingcore.instance.InstanceService
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.onlyOp
import net.geekmc.turingcore.util.lang.Lang
import net.geekmc.turingcore.util.lang.sendLang
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kommand
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@AutoRegister
@OptIn(ExperimentalTime::class)
object CommandSave : Kommand({

    val lang: Lang by turingCoreDi.instance()

    syntax {
        val world = InstanceService.getInstance(InstanceService.MAIN_INSTANCE_ID).apply {
            @Suppress("UnstableApiUsage")
            saveInstance()
        }
        sender.sendLang(lang, "cmd.save.global")
        val time = measureTime {
            world.saveChunksToStorage()
        }.inWholeMilliseconds
        sender.sendLang(lang, "cmd.save.chunks", world.chunks.size, time)
    }.onlyOp()

}, name = "save")