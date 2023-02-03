package net.geekmc.turingcore.player.skin

import kotlinx.coroutines.*
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.event.EventNodes
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.library.service.Service
import net.geekmc.turingcore.util.coroutine.MinestomAsync
import net.geekmc.turingcore.util.coroutine.MinestomSync
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.player.PlayerSkinInitEvent
import world.cepi.kstom.event.listenOnly

/**
 * 基于玩家名的皮肤服务。
 */
@AutoRegister
object SkinService : Service(turingCoreDi) {

    private lateinit var scope: CoroutineScope

    override fun onEnable() {
        scope = CoroutineScope(Dispatchers.MinestomAsync)
        EventNodes.DEFAULT.listenOnly<PlayerSkinInitEvent> {
            scope.launch {
                val skin = withContext(Dispatchers.IO) {
                    PlayerSkin.fromUsername(player.username)
                } ?: return@launch
                withContext(Dispatchers.MinestomSync) {
                    player.skin = skin
                }
            }
        }
    }

    override fun onDisable() {
        scope.cancel()
    }

}