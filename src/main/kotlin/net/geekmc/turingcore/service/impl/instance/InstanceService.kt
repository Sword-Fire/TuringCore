package net.geekmc.turingcore.service.impl.instance

import net.geekmc.turingcore.service.IndependentService
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceManager
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import world.cepi.kstom.Manager

object InstanceService : IndependentService() {

    const val MAIN_INSTANCE_ID = "world"

    private lateinit var manager: InstanceManager
    private lateinit var fullBrightDimension: DimensionType

    private val innerIdToInstanceMap = mutableMapOf<String, Instance>()

    override fun onEnable() {
        manager = MinecraftServer.getInstanceManager()
        // 增加一个具有满亮度的维度。
        fullBrightDimension = DimensionType.builder(NamespaceID.from("full_bright_dimension"))
            .ambientLight(2f)
            .build()
        Manager.dimensionType.addDimension(fullBrightDimension)
    }

    override fun onDisable() {}

    fun createInstanceContainer(name: String): Boolean {
        if (innerIdToInstanceMap.containsKey(name)) {
            throw IllegalArgumentException("(InstanceService) Could not create instance ($name) cause it already exists!")
        }
        innerIdToInstanceMap[name] = manager.createInstanceContainer(fullBrightDimension).apply {
            setGenerator {
                it.modifier().fillHeight(0, 40, Block.GRASS_BLOCK)
            }
        }
        return true
    }

    fun getInstance(name: String): Instance {
        return innerIdToInstanceMap[name] ?: throw IllegalArgumentException("(InstanceService) Instance $name doesn't exist.")
    }
}