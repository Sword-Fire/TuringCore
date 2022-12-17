package net.geekmc.turingcore.block

import net.minestom.server.instance.block.BlockHandler
import world.cepi.kstom.util.register

fun BlockHandler.registerByItNameSpaceId() {
    register(namespaceId)
}