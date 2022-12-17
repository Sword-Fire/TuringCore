package net.geekmc.turingcore.util

import net.minestom.server.extensions.Extension
import java.nio.file.Path

fun Extension.saveResource(resource: String) {
    val target = dataDirectory.resolve(resource)
    if (target.toFile().exists()) {
        return
    }
    savePackagedResource(resource)
}

fun Extension.resolvePath(path: String): Path {
    return dataDirectory.resolve(path)
}