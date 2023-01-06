package net.geekmc.turingcore.service.player_uuid

import net.geekmc.turingcore.db.db
import net.geekmc.turingcore.db.entity.PlayerUuid
import net.geekmc.turingcore.db.table.playerUuids
import net.geekmc.turingcore.service.IndependentService
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import world.cepi.kstom.Manager
import java.util.UUID

/**
 * 存储 玩家 与 UUID 的一对一映射
 *
 * @see [PlayerUuid]
 */
object PlayerUuidService: IndependentService() {
    override fun onEnable() {
        Manager.connection.setUuidProvider { _, username ->
            // 先从数据库中查找，如果不存在就随机生成一个 UUID 并添加进数据库中
            db.playerUuids.find { it.playerName eq username }?.playerUuid ?: UUID.randomUUID().also {
                db.playerUuids.add(PlayerUuid {
                    playerName = username
                    playerUuid = it
                })
            }
        }
    }

    override fun onDisable() {
        error("PlayerUuidService cannot be disabled!")
    }
}