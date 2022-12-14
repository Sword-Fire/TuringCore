package net.geekmc.turingcore

import net.geekmc.turingcore.block.GrassBlockHandler
import net.geekmc.turingcore.command.basic.CommandGamemode
import net.geekmc.turingcore.command.basic.CommandKill
import net.geekmc.turingcore.command.basic.CommandLang
import net.geekmc.turingcore.command.basic.CommandTeleport
import net.geekmc.turingcore.command.debug.CommandInfo
import net.geekmc.turingcore.command.management.CommandOp
import net.geekmc.turingcore.command.management.CommandPermission
import net.geekmc.turingcore.command.management.CommandSave
import net.geekmc.turingcore.command.management.CommandStop
import net.geekmc.turingcore.db.configDatabase
import net.geekmc.turingcore.db.migrateDatabase
import net.geekmc.turingcore.framework.TuringFrameWork
import net.geekmc.turingcore.service.instance.InstanceService
import net.geekmc.turingcore.service.instance.InstanceService.MAIN_INSTANCE_ID
import net.geekmc.turingcore.service.motd.MotdService
import net.geekmc.turingcore.service.player.impl.PlayerBasicDataService
import net.geekmc.turingcore.service.player_uuid.PlayerUuidService
import net.geekmc.turingcore.service.skin.SkinService
import net.geekmc.turingcore.util.GLOBAL_EVENT
import net.geekmc.turingcore.util.color.ColorUtil
import net.geekmc.turingcore.util.color.toComponent
import net.geekmc.turingcore.util.info
import net.geekmc.turingcore.util.lang.LanguageUtil
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extensions.Extension
import net.minestom.server.utils.callback.CommandCallback
import world.cepi.kstom.Manager
import world.cepi.kstom.command.kommand.Kommand
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.util.register
import kotlin.time.ExperimentalTime

class TuringCore : Extension() {

    companion object {
        lateinit var INSTANCE: TuringCore
            private set
    }

    override fun preInitialize() {
        super.preInitialize()
        INSTANCE = this
    }

    override fun initialize() {
        info("TuringCore initializing...")
        initDatabase()
        // ColorUtil ??????????????????????????????
        ColorUtil.init()
        // ???????????????
        LanguageUtil.init()
        // ???????????????
        registerFrameWork()
        // ?????? UUID ?????????
        PlayerUuidService.start()
        // ????????????????????????????????????
        SkinService.start(GLOBAL_EVENT)
        // Motd ?????????
        MotdService.start(GLOBAL_EVENT)
        // ???????????????????????????
        PlayerBasicDataService.start(GLOBAL_EVENT)
        // ???????????????
        InstanceService.start()
        InstanceService.createInstanceContainer(MAIN_INSTANCE_ID)
        val world = InstanceService.getInstance(MAIN_INSTANCE_ID)
        GLOBAL_EVENT.listenOnly<PlayerLoginEvent> {
            setSpawningInstance(world)
            player.respawnPoint = Pos(0.0, 40.0, 0.0)
            player.sendMessage("Welcome to server, ${player.username} !")
        }
        // ???????????????
        registerCommands()
        // ???????????????
        registerBlockHandlers()
        // ???????????????????????????????????????
        GLOBAL_EVENT.listenOnly<PlayerChatEvent> {
            setChatFormat {
                "${player.displayName ?: player.username}: $message".toComponent()
            }
        }
        info("TuringCore initialized.")
    }

    override fun terminate() {}

    private fun initDatabase() {
        migrateDatabase()
        configDatabase()
    }

    private fun registerFrameWork() {
        val registry = TuringFrameWork.registerExtension("net.geekmc.turingcore", this)
        registry.consolePrefix = "[TuringCore] "
        registry.playerPrefix = "&f[&gTuringCore&f] ".toComponent()
    }


    /**
     * ?????? BlockHandler ?????????????????????????????????
     * ??????????????? NID ???????????????????????????????????? BlockHandlerSupplier ?????? BlockHandler???
     * ?????????????????????????????? BlockHandler ??????????????? Tag ????????????????????????
     */
    private fun registerBlockHandlers() {
        GrassBlockHandler.register()
    }

    @OptIn(ExperimentalTime::class)
    private fun registerCommands() {
        Manager.command.unknownCommandCallback = CommandCallback { sender, _ ->
            sender.sendLang("message-command-unknown")
        }
        arrayListOf<Kommand>().apply {
            this += CommandGamemode
            this += CommandKill
            this += CommandTeleport
            this += CommandInfo
            this += CommandOp
            this += CommandPermission
            this += CommandSave
            this += CommandStop
            this += CommandLang
        }.forEach {
            it.register()
        }
    }
}