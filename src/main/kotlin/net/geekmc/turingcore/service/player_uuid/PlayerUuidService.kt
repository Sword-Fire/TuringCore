package net.geekmc.turingcore.service.player_uuid

import net.geekmc.turingcore.db.entity.PlayerUuid
import net.geekmc.turingcore.db.repo.PlayerUuidRepo
import net.geekmc.turingcore.di.DITuringCoreAware
import net.geekmc.turingcore.service.IndependentService
import net.geekmc.turingcore.util.info
import org.kodein.di.instance
import world.cepi.kstom.Manager
import java.util.*

/**
 * 存储 玩家 与 UUID 的一对一映射
 *
 * @see [PlayerUuid]
 */
object PlayerUuidService : IndependentService(), DITuringCoreAware {
    private val playerUuidRepo by instance<PlayerUuidRepo>()

    override fun onEnable() {
        Manager.connection.setUuidProvider { _, username ->
            // 先从数据库中查找，如果不存在就随机生成一个 UUID 并添加进数据库中
            playerUuidRepo.findPlayerUuidByName(username).getOrElse {
                playerUuidRepo.addPlayerUuid {
                    this.uuid = UUID.randomUUID()
                    this.name = username
                }.getOrThrow().also {
                    info { "Generated new UUID for player $username: $it" }
                }
            }.uuid
        }
    }

    override fun onDisable() {
        error("PlayerUuidService cannot be disabled!")
    }
}