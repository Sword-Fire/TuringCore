package net.geekmc.turingcore

import java.nio.file.Path
import kotlin.io.path.outputStream

inline fun <reified T> T.copyResourceToPath(target: Path, resource: String) {
    val inputStream = T::class.java.getResourceAsStream(resource)
    val outputStream = target.outputStream()
    inputStream?.copyTo(outputStream) ?: error("Resource not found: $resource")
}

inline fun <reified T> T.readResourceAsString(resource: String): String {
    val inputStream = T::class.java.getResourceAsStream(resource)
    return inputStream?.bufferedReader()?.readText() ?: error("Resource not found: $resource")
}
