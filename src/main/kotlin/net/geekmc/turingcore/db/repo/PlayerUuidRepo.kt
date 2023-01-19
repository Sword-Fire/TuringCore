package net.geekmc.turingcore.db.repo

import net.geekmc.turingcore.db.entity.PlayerUuid
import net.geekmc.turingcore.db.repo.impl.PlayerUuidRepoImpl
import net.geekmc.turingcore.db.table.PlayerUuids
import java.util.*

/**
 * 玩家与 [UUID] 对应关系仓库
 *
 * @see [PlayerUuid]
 * @see [PlayerUuids]
 * @see [PlayerUuidRepoImpl]
 */
interface PlayerUuidRepo {
    fun findAllPlayerUuids(): Result<List<PlayerUuid>>
    fun findPlayerUuidByUuid(uuid: UUID): Result<PlayerUuid>
    fun findPlayerUuidByName(name: String): Result<PlayerUuid>
    fun addPlayerUuid(block: PlayerUuid.() -> Unit): Result<PlayerUuid>
}