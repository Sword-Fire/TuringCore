package net.geekmc.turingcore.player

import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.color.toComponent
import net.minestom.server.event.player.PlayerChatEvent
import world.cepi.kstom.event.listenOnly

object ChatService : Service() {

    override fun onEnable() {
        EventNodes.DEFAULT.listenOnly<PlayerChatEvent> {
            setChatFormat {
                "${player.displayName ?: player.username}: $message".toComponent()
            }
        }
    }

}