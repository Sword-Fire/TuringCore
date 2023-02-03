package net.geekmc.turingcore.library.service

import net.minestom.server.extensions.Extension
import org.kodein.di.DI
import org.kodein.di.instance

/**
 * 代表一种服务。
 * @param di 依赖注入容器。需要提供 Extension 。
 */
abstract class Service(private val serviceDi: DI) {
    protected val serviceExtension by serviceDi.instance<Extension>()
    protected val serviceLogger by lazy { serviceExtension.logger }

    var isActive = false
        protected set

    fun start() {
        check(!isActive) { "Service is already started!" }
        isActive = true
        onEnable()
    }

    fun stop() {
        check(isActive) { "Service is not started!" }
        isActive = false
        onDisable()
    }

    protected open fun onEnable() {}

    protected open fun onDisable() {}
}