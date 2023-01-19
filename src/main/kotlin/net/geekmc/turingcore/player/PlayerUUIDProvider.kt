package net.geekmc.turingcore.player

import net.minestom.server.entity.Player
import java.util.*

object PlayerUUIDProvider {

    // 通过用户名作种子生成 UUID
    fun getUUID(p: Player): UUID = ("\$player\$" + p.username).toUUID()

    private fun String.toUUID() = UUID.nameUUIDFromBytes(this.toByteArray())

}