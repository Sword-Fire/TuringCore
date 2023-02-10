package net.geekmc.turingcore.util.extender

import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.util.lang.Lang
import net.geekmc.turingcore.util.lang.sendLang
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kommand

/**
 * 注册帮助信息。子命令会继承父命令的帮助信息，但子命令默认不启用 help 命令参数。
 */
fun Kommand.help(action: Kommand.SyntaxContext.() -> Unit) {
    helpAction = action
    default {
        sender.sendLang(KommandExtender.lang, "global.cmd.wrongUsage", context.input)
        helpAction()
    }
    syntax(helpArg) { helpAction() }
}

private object KommandExtender {
    val lang: Lang by turingCoreDi.instance()
}