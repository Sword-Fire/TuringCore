package net.geekmc.turingcore.instance

import net.geekmc.turingcore.ktx.Manager
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceManager
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import java.util.HashMap

// TODO: KDoc
object InstanceService {
    private lateinit var manager: InstanceManager
    private lateinit var fullBrightDimension: DimensionType

    // Instance inner ID -> Instance
    private val map: MutableMap<String, Instance> = HashMap()
    const val MAIN_INSTANCE = "world"

    fun initialize() {
        manager = MinecraftServer.getInstanceManager()
        fullBrightDimension = DimensionType.builder(NamespaceID.from("full_bright_dimension"))
            .ambientLight(2f)
            .build()
        Manager.dimensionType.addDimension(fullBrightDimension)
    }

    fun createInstanceContainer(name: String): Boolean {

        if (map.containsKey(name)) {
            throw IllegalArgumentException("Could not create instance named $name because it already exists!")
        }

        val instance = manager.createInstanceContainer(fullBrightDimension)
        instance.setGenerator { unit: GenerationUnit ->
            unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK)
        }
        map[name] = instance

        return true
    }

    fun getInstance(instanceName: String): Instance {
        if (!map.containsKey(instanceName)) throw IllegalArgumentException("Instance $instanceName does not exist")
        return map[instanceName]!!
    }

}