package net.geekmc.turingcore.framework

import net.minestom.server.extensions.Extension

object TuringFrameWork {

    val frameworkRegistries = hashSetOf<FrameworkRegistry>()

    fun registerExtension(packagePath: String, extension: Extension): FrameworkRegistry {
        val frameworkRegistry = FrameworkRegistry(packagePath, extension)
        frameworkRegistries.add(frameworkRegistry)
        return frameworkRegistry
    }
}