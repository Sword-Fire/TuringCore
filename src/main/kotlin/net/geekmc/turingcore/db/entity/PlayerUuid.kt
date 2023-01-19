package net.geekmc.turingcore.db.entity

import org.ktorm.entity.Entity
import java.util.*

interface PlayerUuid : Entity<PlayerUuid> {
    companion object : Entity.Factory<PlayerUuid>()

    var uuid: UUID
    var name: String
}
