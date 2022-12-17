package net.geekmc.turingcore.service

/**
 * 代表一种没有使用任何 Minestom 事件的服务。
 */
abstract class IndependentService : Service() {

    fun start() {
        if (isActive) {
            throw IllegalStateException("Service is already started!")
        }
        isActive = true
        onEnable()
    }
}