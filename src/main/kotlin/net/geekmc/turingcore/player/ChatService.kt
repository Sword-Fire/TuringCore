package net.geekmc.turingcore.player

import net.geekmc.turingcore.library.color.toComponent
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.event.EventNodes
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.library.service.Service
import net.minestom.server.event.player.PlayerChatEvent
import world.cepi.kstom.event.listenOnly

@AutoRegister
object ChatService : Service(turingCoreDi) {

    override fun onEnable() {
        EventNodes.DEFAULT.listenOnly<PlayerChatEvent> {
            setChatFormat {
                "${player.displayName ?: player.username}: $message".toComponent()
            }
        }
    }

}