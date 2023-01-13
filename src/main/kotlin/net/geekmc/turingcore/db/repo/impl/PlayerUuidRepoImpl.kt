package net.geekmc.turingcore.db.repo.impl

import net.geekmc.turingcore.db.entity.PlayerUuid
import net.geekmc.turingcore.db.repo.PlayerUuidRepo
import net.geekmc.turingcore.db.table.playerUuids
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.toList
import java.util.*

/**
 * @see [PlayerUuidRepo]
 */
class PlayerUuidRepoImpl(private val db: Database) : PlayerUuidRepo {
    override fun findAllPlayerUuids(): Result<List<PlayerUuid>> = runCatching {
        db.playerUuids.toList()
    }

    override fun findPlayerUuidByUuid(uuid: UUID): Result<PlayerUuid> = runCatching {
        db.playerUuids.find { it.uuid eq uuid }
            ?: throw IllegalArgumentException("Player with UUID $uuid not found")
    }

    override fun findPlayerUuidByName(name: String): Result<PlayerUuid> = runCatching {
        db.playerUuids.find { it.name eq name }
            ?: throw IllegalArgumentException("Player with name $name not found")
    }

    override fun addPlayerUuid(block: PlayerUuid.() -> Unit): Result<PlayerUuid> = runCatching {
        PlayerUuid.invoke(block).also {
            db.playerUuids.add(it)
        }
    }
}