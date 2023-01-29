package net.geekmc.turingcore.framework

import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.extender.info
import net.minestom.server.command.builder.Command
import net.minestom.server.extensions.Extension
import net.minestom.server.instance.block.BlockHandler
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import org.slf4j.Logger
import world.cepi.kstom.command.kommand.Kommand
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.kstom.util.register
import kotlin.reflect.KClass

/**
 * 自动注册优先度
 *
 * @param priority 优先度
 */
enum class AutoRegisterPriority(private val priority: Long) {
    HIGHEST(Long.MAX_VALUE),
    HIGH(+1000),
    DEFAULT(0),
    LOW(-1000),
    LOWEST(Long.MIN_VALUE)
}

/**
 * 自动注册标记注解
 *
 * @see [AutoRegisterFramework]
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class AutoRegister(
    val priority: AutoRegisterPriority = AutoRegisterPriority.DEFAULT
)


private class AutoRegisterScanner(
    private val classLoader: ClassLoader
) {
    fun scanClazzes(basePackageName: String): List<KClass<*>> {
        val reflections = Reflections(
            ConfigurationBuilder
                .build(basePackageName)
                .setClassLoaders(arrayOf(classLoader))
        )
        return reflections.getTypesAnnotatedWith(AutoRegister::class.java).map {
            it.kotlin
        }
    }
}

/**
 * 自动注册框架，用于自动注册指令、服务与方块处理器等。
 *
 * @sample [AutoRegisterExtension]
 */
class AutoRegisterFramework(
    classLoader: ClassLoader,
    private val basePackageName: String,
    private val logger: Logger? = null
) {
    private enum class AutoRegisterType {
        COMMAND,
        KOMMAND,
        SERVICE,
        BLOCK_HANDLER
    }

    private val scanner = AutoRegisterScanner(classLoader)
    private val registerObjs by lazy {
        scanner.scanClazzes(basePackageName).map {
            it.objectInstance ?: error("AutoRegister class ${it.qualifiedName} must be an object.")
        }.sortedBy { item ->
            ((item::class.annotations.first { it is AutoRegister }) as AutoRegister).priority
        }.groupBy {
            when (it) {
                is Command -> AutoRegisterType.COMMAND
                is Kommand -> AutoRegisterType.KOMMAND
                is Service -> AutoRegisterType.SERVICE
                is BlockHandler -> AutoRegisterType.BLOCK_HANDLER
                else -> error("Unsupported AutoRegister type: ${it::class.qualifiedName}.")
            }
        }
    }

    private val commandObjs
        get() = registerObjs[AutoRegisterType.COMMAND] ?: emptyList()

    private val kommandObjs
        get() = registerObjs[AutoRegisterType.KOMMAND] ?: emptyList()

    private val serviceObjs
        get() = registerObjs[AutoRegisterType.SERVICE] ?: emptyList()

    private val blockHandlerObjs
        get() = registerObjs[AutoRegisterType.BLOCK_HANDLER] ?: emptyList()

    /**
     * 注册所有命令
     */
    fun registerCommands() {
        commandObjs.forEach {
            logger?.info { "Registering command ${it::class.qualifiedName}." }
            (it as Command).register()
        }
        kommandObjs.forEach {
            logger?.info { "Registering kommand ${it::class.qualifiedName}." }
            (it as Kommand).register()
        }
    }

    /**
     * 反注册所有命令
     */
    fun unregisterCommands() {
        kommandObjs.reversed().forEach {
            logger?.info { "Unregistering kommand ${it::class.qualifiedName}." }
            (it as Kommand).unregister()
        }
        commandObjs.reversed().forEach {
            logger?.info { "Unregistering command ${it::class.qualifiedName}." }
            (it as Command).unregister()
        }
    }

    /**
     * 启动所有服务
     */
    fun startServices() {
        serviceObjs.forEach {
            logger?.info { "Starting service ${it::class.qualifiedName}." }
            (it as Service).start()
        }
    }

    /**
     * 停止所有服务
     */
    fun stopServices() {
        serviceObjs.reversed().forEach {
            logger?.info { "Stopping service ${it::class.qualifiedName}." }
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
            logger?.info { "Registering block handler ${it::class.qualifiedName}." }
            (it as BlockHandler).register()
        }
    }

    /**
     * 注册所有标记了 [AutoRegister] 的项目
     */
    fun registerAll() {
        startServices()
        registerCommands()
        registerBlockHandlers()
    }

    /**
     * 反注册所有标记了 [AutoRegister] 的项目
     *
     * 由于 [BlockHandler] 无法反注册，因此不会反注册 [BlockHandler]
     */
    fun unregisterAll() {
        unregisterCommands()
        stopServices()
        logger?.info("Block handlers are not able to unregister.")
    }
}

/**
 * 作为一个扩展的基类，自动注册所有标记了 [AutoRegister] 的类
 *
 * @param [basePackageName] 扫描的包名
 * @see [AutoRegisterFramework]
 */
abstract class AutoRegisterExtension(basePackageName: String) : Extension() {
    private val autoRegisterFramework =
        AutoRegisterFramework(origin.classLoader, basePackageName, logger)

    override fun initialize() {
        autoRegisterFramework.registerAll()
    }

    override fun terminate() {
        autoRegisterFramework.unregisterAll()
    }
}