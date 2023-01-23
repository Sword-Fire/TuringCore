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
import net.geekmc.turingcore.data.PlayerDataService
import net.geekmc.turingcore.di.initTuringCoreDi
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.framework.TuringFrameWork
import net.geekmc.turingcore.instance.InstanceService
import net.geekmc.turingcore.instance.InstanceService.MAIN_INSTANCE_ID
import net.geekmc.turingcore.motd.MotdService
import net.geekmc.turingcore.player.EssentialPlayerDataService
import net.geekmc.turingcore.skin.SkinService
import net.geekmc.turingcore.util.color.ColorUtil
import net.geekmc.turingcore.util.color.toComponent
import net.geekmc.turingcore.util.lang.LanguageUtil
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extensions.Extension
import net.minestom.server.utils.callback.CommandCallback
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.util.register

class TuringCore : Extension() {
    override fun preInitialize() {
        super.preInitialize()
        initTuringCoreDi(this)
    }

    override fun initialize() {
        logger.info("TuringCore initializing...")
        // 事件节点服务。
        EventNodes.start()
        // ColorUtil 在这里的优先级最高。
        ColorUtil.init()
        // 语言工具。
        LanguageUtil.init()
        // 注册框架。
        registerFrameWork()
        // 皮肤服务。（基于玩家名）
        SkinService.start()
        // Motd 服务。
        MotdService.start()
        // 所有种类的玩家信息服务。
        PlayerDataService.start()
        // 玩家基础信息服务。
        EssentialPlayerDataService.start()
        // 世界服务。
        InstanceService.start()
        InstanceService.createInstanceContainer(MAIN_INSTANCE_ID)
        val world = InstanceService.getInstance(MAIN_INSTANCE_ID)
        EventNodes.INTERNAL_HIGHEST.listenOnly<PlayerLoginEvent> {
//            println("listen internal highest player login event")
            setSpawningInstance(world)
//            player.respawnPoint = Pos(0.0, 40.0, 0.0)
            player.sendMessage("Welcome to server, ${player.username} !")
        }
        // 注册指令。
        registerCommands()
        // 注册方块。
        registerBlockHandlers()
        // 处理玩家聊天的临时监听器。
        EventNodes.DEFAULT.listenOnly<PlayerChatEvent> {
            setChatFormat {
                "${player.displayName ?: player.username}: $message".toComponent()
            }
        }
        logger.info("TuringCore initialized.")
    }

    override fun terminate() {}

    private fun registerFrameWork() {
        TuringFrameWork.registerExtension("net.geekmc.turingcore", this).apply {
            consolePrefix = "[TuringCore] "
            playerPrefix = "&f[&gTuringCore&f] ".toComponent()
        }
    }


    /**
     * 注册 BlockHandler 的意义是在读取地图时，
     * 会用方块的 NID 作为键值查找是否有对应的 BlockHandlerSupplier 提供 BlockHandler。
     * 并且保存世界时，只有 BlockHandler 里注册过的 Tag 会被存到地图里。
     */
    private fun registerBlockHandlers() {
        GrassBlockHandler.register()
    }

    private fun registerCommands() {
        // 输入未知指令时的提示信息。
        Manager.command.unknownCommandCallback = CommandCallback { sender, _ ->
            sender.sendLang("message-command-unknown")
        }
        // 注册指令。
        arrayListOf(
            CommandGamemode,
            CommandKill,
            CommandLang,
            CommandTeleport,
            CommandInfo,
            CommandOp,
            CommandPermission,
            CommandSave,
            CommandStop
        ).forEach { it.register() }
    }
}