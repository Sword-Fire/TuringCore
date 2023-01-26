package net.geekmc.turingcore.service

import net.geekmc.turingcore.di.TuringCoreDIAware
import org.kodein.di.instance
import org.slf4j.Logger

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