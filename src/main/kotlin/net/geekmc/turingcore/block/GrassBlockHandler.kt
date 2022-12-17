package net.geekmc.turingcore.block

import net.geekmc.turingcore.util.info
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.utils.NamespaceID

object GrassBlockHandler : BlockHandler {

    override fun getNamespaceId() = NamespaceID.from("minecraft:grass_block")

    override fun onDestroy(destroy: BlockHandler.Destroy) {
        super.onDestroy(destroy)
        info("Grass-block destroyed.")
    }
}