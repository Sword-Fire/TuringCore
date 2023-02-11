package net.geekmc.turingcore

import net.geekmc.turingcore.instance.InstanceService
import net.geekmc.turingcore.library.color.ColorService
import net.geekmc.turingcore.library.data.global.GlobalDataService
import net.geekmc.turingcore.library.data.player.PlayerDataService
import net.geekmc.turingcore.library.di.initTuringCoreDi
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.event.EventNodes
import net.geekmc.turingcore.library.framework.AutoRegisterFramework
import net.geekmc.turingcore.player.uuid.UUIDService
import net.geekmc.turingcore.util.lang.GlobalLang
import net.geekmc.turingcore.util.lang.LanguageService
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.extensions.Extension
import net.minestom.server.utils.callback.CommandCallback
import org.kodein.di.instance
import world.cepi.kstom.Manager

@Suppress("unused")
class TuringCore : Extension() {

    private val autoRegisterFramework by lazy {
        AutoRegisterFramework.load(
            this::class.java.classLoader,
            "net.geekmc.turingcore",
            this.logger
        )
    }
    private val globalLang: GlobalLang by turingCoreDi.instance()

    override fun preInitialize() {
        initTuringCoreDi(this)
    }

    override fun initialize() {
        logger.info("--- TuringCore start initialization ---")
        ColorService.start()
        LanguageService.start()
        UUIDService.start()
        EventNodes.start()
        GlobalDataService.start()
        PlayerDataService.start()
        InstanceService.start()
        autoRegisterFramework.registerAll()
        registerUnknownCommandCallback()
        logger.info("--- TuringCore finish initialization ---")
    }

    override fun terminate() {
        logger.info("--- TuringCore start termination ---")
        InstanceService.stop()
        PlayerDataService.stop()
        GlobalDataService.stop()
        EventNodes.stop()
        UUIDService.stop()
        LanguageService.stop()
        ColorService.stop()
        autoRegisterFramework.unregisterAll()
        logger.info("--- TuringCore finish termination ---")
    }

    private fun registerUnknownCommandCallback() {
        // 输入未知指令时的提示信息。
        Manager.command.unknownCommandCallback = CommandCallback { sender, _ ->
            sender.sendLang(globalLang, "global.cmd.unknown")
        }
    }

}