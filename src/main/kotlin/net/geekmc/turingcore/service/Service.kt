package net.geekmc.turingcore.service

/**
 * 代表一种服务。
 * @see MinestomService
 * @see IndependentService
 */
abstract class Service {

    var isActive = false
        protected set

    fun stop() {
        if (!isActive) {
            throw IllegalStateException("Service is not started!")
        }
        isActive = false
        onDisable()
    }

    protected abstract fun onEnable()

    protected abstract fun onDisable()
}