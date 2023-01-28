package net.geekmc.turingcore.instance

import net.geekmc.turingcore.di.TuringCoreDIAware
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.framework.AutoRegister
import net.geekmc.turingcore.service.Service
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceManager
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import org.kodein.di.instance
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly

@AutoRegister
object InstanceService : Service(), TuringCoreDIAware {

    const val MAIN_INSTANCE_ID = "world"

    private val instanceManager: InstanceManager by instance()
    private lateinit var fullBrightDimension: DimensionType

    private val innerIdToInstanceMap = mutableMapOf<String, Instance>()

    override fun onEnable() {

        // 增加一个具有满亮度的维度。
        fullBrightDimension = DimensionType.builder(NamespaceID.from("full_bright_dimension"))
            .ambientLight(2f)
            .build()
        Manager.dimensionType.addDimension(fullBrightDimension)

        // 临时创建一个世界。
        createInstanceContainer(MAIN_INSTANCE_ID)
        val world = getInstance(MAIN_INSTANCE_ID)
        EventNodes.INTERNAL_HIGHEST.listenOnly<PlayerLoginEvent> {
            setSpawningInstance(world)
            player.sendMessage("Welcome to server, ${player.username} !")
        }
    }

    fun createInstanceContainer(name: String): Boolean {
        if (innerIdToInstanceMap.containsKey(name)) {
            throw IllegalArgumentException("(InstanceService) Could not create instance ($name) cause it already exists!")
        }
        innerIdToInstanceMap[name] = instanceManager.createInstanceContainer(fullBrightDimension).apply {
            setGenerator {
                it.modifier().fillHeight(0, 40, Block.GRASS_BLOCK)
            }
        }
        return true
    }

    fun getInstance(name: String): Instance {
        return innerIdToInstanceMap[name]
            ?: throw IllegalArgumentException("(InstanceService) Instance $name doesn't exist.")
    }
}