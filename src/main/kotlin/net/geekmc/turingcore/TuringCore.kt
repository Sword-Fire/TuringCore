package net.geekmc.turingcore

import net.geekmc.turingcore.di.initTuringCoreDi
import net.geekmc.turingcore.framework.AutoRegisterFramework
import net.geekmc.turingcore.framework.TuringFrameWork
import net.geekmc.turingcore.util.color.toComponent
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.extensions.Extension
import net.minestom.server.utils.callback.CommandCallback
import world.cepi.kstom.Manager

@Suppress("unused")
class TuringCore : Extension() {
    override fun preInitialize() {
        initTuringCoreDi(this)
    }

    private val autoRegisterFramework by lazy {
        AutoRegisterFramework(javaClass.classLoader, "net.geekmc.turingcore", logger)
    }

    private fun autoRegister() {
        autoRegisterFramework.registerAll()
    }

    private fun autoUnregister() {
        autoRegisterFramework.unregisterAll()
    }

    override fun initialize() {
        logger.info("TuringCore initializing...")
        autoRegister()
        registerUnknownCommandCallback()

        logger.info("TuringCore initialized.")
    }


    override fun terminate() {
        autoUnregister()
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