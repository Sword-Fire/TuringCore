package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.minestom.server.command.builder.arguments.ArgumentString
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandLang : Kommand({

    val reloadArgument = ArgumentString("reload")

    val lang: Lang by turingCoreDi.instance()

    opSyntax(reloadArgument) {
        sender.sendLang(lang, "lang.cmd.reload")
    }
}, name = "lang")