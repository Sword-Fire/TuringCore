package net.geekmc.turingcore.level

import net.geekmc.turingcore.library.di.TuringCoreDIAware
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.help
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandLevel: Kommand({
    help {
        sender.sendMessage("错误的命令: ${context.input}")
        sender.sendMessage("Usage: level help")
        sender.sendMessage("Usage: level level see|set|add|remove [player] <level>")
        sender.sendMessage("Usage: level exp see|set|add|remove [player] <level>")
    }


}, name = "level"), TuringCoreDIAware