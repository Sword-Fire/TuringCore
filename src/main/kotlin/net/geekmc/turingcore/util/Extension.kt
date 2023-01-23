package net.geekmc.turingcore.util

import net.minestom.server.extensions.Extension

fun Extension.saveResource(resource: String) {
    val target = dataDirectory.resolve(resource)
    if (target.toFile().exists()) {
        return
    }
    savePackagedResource(resource)
}
