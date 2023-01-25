package net.geekmc.turingcore.service

import net.geekmc.turingcore.di.DITuringCoreAware
import org.kodein.di.instance
import org.slf4j.Logger

/**
 * 代表一种服务。
 */
abstract class Service : DITuringCoreAware {
    protected val logger: Logger by instance()

    var isActive = false
        protected set

    open fun start() {
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