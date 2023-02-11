package net.geekmc.turingcore.library.framework

import net.geekmc.turingcore.library.data.global.GlobalData
import net.geekmc.turingcore.library.data.global.GlobalDataService
import net.geekmc.turingcore.library.data.player.PlayerData
import net.geekmc.turingcore.library.data.player.PlayerDataService
import net.geekmc.turingcore.library.service.Service
import net.minestom.server.command.builder.Command
import net.minestom.server.instance.block.BlockHandler
import org.slf4j.Logger
import world.cepi.kstom.command.kommand.Kommand
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.kstom.util.register
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * 自动注册框架，用于自动注册指令、服务与方块处理器等。
 */
class AutoRegisterFramework(
    classLoader: ClassLoader,
    private val basePackageName: String,
    private val logger: Logger? = null
) {

    companion object {
        /**
         * 注册在指定包路径下所有标记了 [AutoRegister] 的可自动注册类。
         *
         * @param extension 扩展
         * @param basePackageName 扫描的包名
         * @see [AutoRegisterFramework]
         */
        fun load(classLoader: ClassLoader, basePackageName: String, logger: Logger? = null): AutoRegisterFramework {
            return AutoRegisterFramework(classLoader, basePackageName, logger)
        }
    }

    private enum class AutoRegisterType {
        COMMAND,
        KOMMAND,
        SERVICE,
        BLOCK_HANDLER,
        PLAYER_DATA,
        GLOBAL_DATA
    }

    private val List<KClass<*>>.objects: List<Any>
        get() = map {
            it.objectInstance ?: error("AutoRegister class ${it.qualifiedName} must be an object.")
        }

    private val scanner = AutoRegisterScanner(classLoader)
    private val registerClasses by lazy {
        scanner.scanClasses(basePackageName).groupBy {
            when {
                it.isSubclassOf(Command::class) -> AutoRegisterType.COMMAND
                it.isSubclassOf(Kommand::class) -> AutoRegisterType.KOMMAND
                it.isSubclassOf(Service::class) -> AutoRegisterType.SERVICE
                it.isSubclassOf(BlockHandler::class) -> AutoRegisterType.BLOCK_HANDLER
                it.isSubclassOf(PlayerData::class) -> AutoRegisterType.PLAYER_DATA
                it.isSubclassOf(GlobalData::class) -> AutoRegisterType.GLOBAL_DATA
                else -> error("Unsupported AutoRegister type: ${it.qualifiedName}.")
            }
        }
    }

    private val commandObjs
        get() = registerClasses[AutoRegisterType.COMMAND]?.objects ?: emptyList()

    private val kommandObjs
        get() = registerClasses[AutoRegisterType.KOMMAND]?.objects ?: emptyList()

    private val serviceObjs
        get() = registerClasses[AutoRegisterType.SERVICE]?.objects ?: emptyList()

    private val blockHandlerObjs
        get() = registerClasses[AutoRegisterType.BLOCK_HANDLER]?.objects ?: emptyList()

    @Suppress("UNCHECKED_CAST")
    private val playerDataClasses
        get() = registerClasses[AutoRegisterType.PLAYER_DATA] as? List<KClass<PlayerData>> ?: emptyList()

    @Suppress("UNCHECKED_CAST")
    private val globalDataClassess
        get() = registerClasses[AutoRegisterType.GLOBAL_DATA] as? List<KClass<GlobalData>> ?: emptyList()

    private val KClass<*>.identifer
        get() = ((this.annotations.first { it is AutoRegister }) as AutoRegister).id

    /**
     * 注册所有命令
     */
    fun registerCommands() {
        commandObjs.forEach {
            (it as Command).register()
        }
        kommandObjs.forEach {
            (it as Kommand).register()
        }
    }

    /**
     * 反注册所有命令
     */
    fun unregisterCommands() {
        kommandObjs.reversed().forEach {
            (it as Kommand).unregister()
        }
        commandObjs.reversed().forEach {
            (it as Command).unregister()
        }
    }

    /**
     * 启动所有服务
     */
    fun startServices() {
        serviceObjs.forEach {
            (it as Service).start()
        }
    }

    /**
     * 停止所有服务
     */
    fun stopServices() {
        serviceObjs.reversed().forEach {
            (it as Service).stop()
        }
    }

    /**
     * 注册所有 [BlockHandler]
     *
     * 注意，[BlockHandler] 无法反注册
     */
    fun registerBlockHandlers() {
        blockHandlerObjs.forEach {
            (it as BlockHandler).register()
        }
    }

    fun registerPlayerData() {
        playerDataClasses.forEach {
            require(it.identifer.isNotBlank()) { "Player data ${it.qualifiedName} must have an identifier." }
            PlayerDataService.register(it, it.identifer)
        }
    }

    fun registerGlobalData() {
        globalDataClassess.forEach {
            require(it.identifer.isNotBlank()) { "Global data ${it.qualifiedName} must have an identifier." }
            GlobalDataService.register(it, it.identifer)
        }
    }

    /**
     * 注册所有标记了 [AutoRegister] 的项目
     */
    fun registerAll() {
        startServices()
        registerGlobalData()
        registerPlayerData()
        registerCommands()
        registerBlockHandlers()
    }

    /**
     * 反注册所有标记了 [AutoRegister] 的项目
     *
     * 由于无法反注册，因此不会反注册 [BlockHandler] [PlayerData] [GlobalData]
     */
    fun unregisterAll() {
        unregisterCommands()
        stopServices()
    }

}