package net.geekmc.turingcore.event

import net.geekmc.turingcore.service.Service
import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

/**
 * 类似Bukkit的事件优先级系统。
 */
object EventNodes : Service() {
    /**
     * 使用[INTERNAL_HIGHEST]和[INTERNAL_LOWEST]时必须在此处声明，以管理优先级。
     * [PlayerDataService]使用这两个节点在[AsyncPlayerPreLoginEvent]与[PlayerDisconnectEvent]中读取/存放玩家数据。
     */
    lateinit var INTERNAL_HIGHEST: EventNode<Event>
    lateinit var VERY_HIGH: EventNode<Event>
    lateinit var HIGH: EventNode<Event>
    lateinit var DEFAULT: EventNode<Event>
    lateinit var LOW: EventNode<Event>
    lateinit var VERY_LOW: EventNode<Event>
    lateinit var INTERNAL_LOWEST: EventNode<Event>

    override fun onEnable() {
        INTERNAL_HIGHEST = MinecraftServer.getGlobalEventHandler()

        VERY_HIGH = EventNode.all("very_high")
        INTERNAL_HIGHEST.addChild(VERY_HIGH)

        HIGH = EventNode.all("high")
        VERY_HIGH.addChild(HIGH)

        DEFAULT = EventNode.all("default")
        HIGH.addChild(DEFAULT)

        LOW = EventNode.all("low")
        DEFAULT.addChild(LOW)

        VERY_LOW = EventNode.all("very_low")
        LOW.addChild(VERY_LOW)

        INTERNAL_LOWEST = EventNode.all("internal_lowest")
        VERY_LOW.addChild(INTERNAL_LOWEST)
    }
}