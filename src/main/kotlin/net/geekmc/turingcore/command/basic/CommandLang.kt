package net.geekmc.turingcore.command.basic

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.lang.ExtensionLang
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.ArgumentString
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandLang : Kommand({

    val reloadArgument = ArgumentString("reload")

    val lang by turingCoreDi.instance<ExtensionLang>()

    opSyntax(reloadArgument) {
        sender.sendLang(lang, "message-command-lang-reload-succ")
    }
}, name = "lang")