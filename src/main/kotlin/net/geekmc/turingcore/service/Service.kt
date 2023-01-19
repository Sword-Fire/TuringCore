package net.geekmc.turingcore.service

/**
 * 代表一种服务。
 * @see [EventService]
 */
abstract class Service {

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