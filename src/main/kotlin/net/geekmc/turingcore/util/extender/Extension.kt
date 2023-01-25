package net.geekmc.turingcore.util.extender

import net.minestom.server.extensions.Extension
import kotlin.io.path.exists

fun Extension.saveResource(resource: String, throwWhenExists: Boolean = false) {
    val target = dataDirectory.resolve(resource)
    if (target.exists()) {
        throwWhenExists && error("Resource $resource already exists!")
    }
    savePackagedResource(resource)
}
