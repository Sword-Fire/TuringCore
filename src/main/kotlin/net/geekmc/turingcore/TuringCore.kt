package net.geekmc.turingcore

import net.geekmc.turingcore.di.initTuringCoreDi
import net.geekmc.turingcore.framework.AutoRegisterExtension
import net.geekmc.turingcore.framework.TuringFrameWork
import net.geekmc.turingcore.util.color.toComponent
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.utils.callback.CommandCallback
import world.cepi.kstom.Manager

@Suppress("unused")
class TuringCore : AutoRegisterExtension("net.geekmc.turingcore") {
    override fun preInitialize() {
        initTuringCoreDi(this)
    }

    override fun initialize() {
        super.initialize()
        logger.info("TuringCore initializing...")
        registerUnknownCommandCallback()

        logger.info("TuringCore initialized.")
    }

    private fun registerFrameWork() {
        TuringFrameWork.registerExtension("net.geekmc.turingcore", this).apply {
            consolePrefix = "[TuringCore] "
            playerPrefix = "&f[&gTuringCore&f] ".toComponent()
        }
    }

    private fun registerUnknownCommandCallback() {
        // 输入未知指令时的提示信息。
        Manager.command.unknownCommandCallback = CommandCallback { sender, _ ->
            sender.sendLang("message-command-unknown")
        }
    }
}