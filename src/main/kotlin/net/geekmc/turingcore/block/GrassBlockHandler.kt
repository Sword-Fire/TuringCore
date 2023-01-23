package net.geekmc.turingcore.block

import net.geekmc.turingcore.di.DITuringCoreAware
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.utils.NamespaceID
import org.kodein.di.instance
import org.slf4j.Logger

object GrassBlockHandler : BlockHandler, DITuringCoreAware {
    override fun getNamespaceId() = NamespaceID.from("minecraft:grass_block")
    private val logger: Logger by instance()

    override fun onDestroy(destroy: BlockHandler.Destroy) {
        super.onDestroy(destroy)
        logger.info("Grass block destroyed.")
    }
}