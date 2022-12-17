package net.geekmc.turingcore.command.basic

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.util.color.message
import net.geekmc.turingcore.util.lang.LanguageUtil
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.ArgumentString
import world.cepi.kstom.command.kommand.Kommand

object CommandLang : Kommand({

    val reloadArgument = ArgumentString("reload")

    opSyntax(reloadArgument) {
        LanguageUtil.init()
        sender.sendLang("message-command-lang-reload-succ")
    }
}, name = "lang")