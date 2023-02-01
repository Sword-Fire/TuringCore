package net.geekmc.turingcore.library.service

import net.geekmc.turingcore.library.di.TuringCoreDIAware
import org.kodein.di.instance
import org.slf4j.Logger

// TODO: 其他插件也有服务，不应该使用TuringCoreDIAware而是注入DI.
/**
 * 代表一种服务。
 */
abstract class Service : TuringCoreDIAware {
    protected val logger: Logger by instance()

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