package net.geekmc.turingcore.service

import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

/**
 * 代表一种使用了 Minestom 事件的服务。
 */
abstract class MinestomService : Service() {

    protected lateinit var eventNode: EventNode<Event>

    /**
     * 启用该服务，并使所有监听器都挂载在 [eventNode] 中。
     */
    fun start(eventNode: EventNode<Event>) {
        if (isActive) {
            throw IllegalStateException("Service is already started!")
        }
        isActive = true
        this.eventNode = eventNode
        onEnable()
    }
}