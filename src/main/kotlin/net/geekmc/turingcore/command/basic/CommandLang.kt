package net.geekmc.turingcore.command.basic

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.ArgumentString
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandLang : Kommand({

    val reloadArgument = ArgumentString("reload")

    opSyntax(reloadArgument) {
        sender.sendLang("message-command-lang-reload-succ")
    }
}, name = "lang")