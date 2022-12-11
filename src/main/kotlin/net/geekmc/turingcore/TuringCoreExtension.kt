package net.geekmc.turingcore

import net.geekmc.turingcore.command.commands
import net.minestom.server.extensions.Extension
import org.slf4j.LoggerFactory

@Suppress("unused")
class TuringCoreExtension: Extension() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TuringCoreExtension::class.java)
    }

    private fun registerCommands() {
        LOGGER.debug("Registering commands...")
        commands.forEach { it.register() }
    }

    override fun initialize() {
        LOGGER.debug("TuringCore initializing...")
        registerCommands()
    }

    override fun terminate() {
        LOGGER.debug("TuringCore terminating...")
    }
}