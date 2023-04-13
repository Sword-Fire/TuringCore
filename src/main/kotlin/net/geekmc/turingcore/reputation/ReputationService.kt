package net.geekmc.turingcore.reputation

import net.geekmc.turingcore.library.config.ConfigService
import net.geekmc.turingcore.library.data.player.PlayerDataLoadEvent
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.event.EventNodes
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.library.service.Service
import net.minestom.server.extensions.Extension
import org.kodein.di.instance
import world.cepi.kstom.event.listenOnly

@AutoRegister
object ReputationService : Service(turingCoreDi) {
    private val extension: Extension by instance()
    private lateinit var config: ReputationConfig

    override fun onEnable() {
        loadConfig()
        EventNodes.VERY_HIGH.listenOnly<PlayerDataLoadEvent> {
            val data = getData<ReputationData>()
        }
    }

    private fun loadConfig() {
        config = ConfigService.loadConfig(extension, "reputations.yml")
    }
}