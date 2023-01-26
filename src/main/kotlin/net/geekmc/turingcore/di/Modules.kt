@file:Suppress("RemoveExplicitTypeArguments")

package net.geekmc.turingcore.di

import net.minestom.server.extensions.Extension
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.slf4j.Logger
import java.nio.file.Path

enum class PathKey {
    EXTENSION_FOLDER,
}

val baseModule by DI.Module {
    bindSingleton<Logger> { instance<Extension>().logger }
    bindSingleton<Path>(tag = PathKey.EXTENSION_FOLDER) { instance<Extension>().dataDirectory }
}
