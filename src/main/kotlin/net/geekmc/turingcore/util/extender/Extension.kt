package net.geekmc.turingcore.util.extender

import net.minestom.server.extensions.Extension
import java.nio.file.Path
import kotlin.io.path.exists

fun Extension.saveResource(resource: String, throwWhenExists: Boolean = false): Boolean =
    saveResource(Path.of(resource), throwWhenExists)

fun Extension.saveResource(resourcePath: Path, throwWhenExists: Boolean = false): Boolean {
    val target = dataDirectory / resourcePath
    return if (target.exists()) {
        throwWhenExists && error("Resource $resourcePath already exists!")
        false
    } else {
        savePackagedResource(resourcePath)
    }
}
