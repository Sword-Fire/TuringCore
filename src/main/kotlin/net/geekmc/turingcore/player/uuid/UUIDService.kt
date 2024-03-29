package net.geekmc.turingcore.player.uuid

import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.service.Service
import net.minestom.server.entity.Player
import world.cepi.kstom.Manager
import java.util.*

object UUIDService : Service(turingCoreDi) {
    override fun onEnable() {
        Manager.connection.setUuidProvider { _, username ->
            getUUID(username)
        }
    }

    fun getUUID(p: Player): UUID = getUUID(p.username)

    fun getUUID(username: String): UUID = ("\$player\$$username").toUUID()

    private fun String.toUUID() = UUID.nameUUIDFromBytes(this.toByteArray())
}