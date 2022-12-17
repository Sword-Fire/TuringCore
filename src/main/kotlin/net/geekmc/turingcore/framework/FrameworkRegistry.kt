package net.geekmc.turingcore.framework

import net.kyori.adventure.text.Component
import net.minestom.server.extensions.Extension

class FrameworkRegistry(val packagePath: String, var extension: Extension) {

    var consolePrefix: String = ""
    var playerPrefix: Component = Component.text("")

    fun destroy() {
        TuringFrameWork.frameworkRegistries.remove(this)
    }
}