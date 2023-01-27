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
import net.geekmc.turingcore.data.global.GlobalDataService
import net.geekmc.turingcore.data.player.PlayerDataService
import net.geekmc.turingcore.di.initTuringCoreDi
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.framework.TuringFrameWork
import net.geekmc.turingcore.instance.InstanceService
import net.geekmc.turingcore.motd.MotdService
import net.geekmc.turingcore.player.ChatService
import net.geekmc.turingcore.player.essentialdata.EssentialPlayerDataService
import net.geekmc.turingcore.player.skin.SkinService
import net.geekmc.turingcore.player.uuid.UUIDService
import net.geekmc.turingcore.util.color.ColorService
import net.geekmc.turingcore.util.color.toComponent
import net.geekmc.turingcore.util.lang.LanguageUtil
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.extensions.Extension
import net.minestom.server.utils.callback.CommandCallback
import world.cepi.kstom.Manager
import world.cepi.kstom.util.register

class TuringCore : Extension() {
    override fun preInitialize() {
        super.preInitialize()
        initTuringCoreDi(this)
    }

    override fun initialize() {
        logger.info("TuringCore initializing...")
        // 颜色服务。高优先级。
        ColorService.start()
        // UUID 服务。
        UUIDService.start()
        // 事件节点服务。
        EventNodes.start()
        // 语言工具。
        LanguageUtil.init()
        // 注册框架。
        registerFrameWork()
        // 皮肤服务。
        SkinService.start()
        // Motd 服务。
        MotdService.start()
        // 全局数据服务。
        GlobalDataService.start()
        // 玩家数据服务。
        PlayerDataService.start()
        // 玩家基础数据服务。
        EssentialPlayerDataService.start()
        // 聊天服务。
        ChatService.start()
        // 世界服务。
        InstanceService.start()
        // 注册指令。
        registerCommands()
        // 注册方块。
        registerBlockHandlers()

        logger.info("TuringCore initialized.")
    }


    override fun terminate() {
        // 必须按启动顺序的逆顺序关闭。
        InstanceService.stop()
        ChatService.stop()
        EssentialPlayerDataService.stop()
        PlayerDataService.stop()
        MotdService.stop()
        SkinService.stop()
        EventNodes.stop()
        UUIDService.stop()
        ColorService.stop()
    }

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