package net.geekmc.turingcore.level

import net.geekmc.turingcore.coin.CoinData
import net.geekmc.turingcore.library.config.ConfigService
import net.geekmc.turingcore.library.data.player.PlayerDataLoadEvent
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.event.EventNodes
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.library.service.Service
import net.minestom.server.entity.Player
import net.minestom.server.extensions.Extension
import org.kodein.di.instance
import world.cepi.kstom.event.listenOnly

@AutoRegister
object LevelService : Service(turingCoreDi) {
    private val extension: Extension by instance()
    private lateinit var config: LevelConfig

    override fun onEnable() {
        loadConfig()
        EventNodes.VERY_HIGH.listenOnly<PlayerDataLoadEvent> {
            val data = getData<CoinData>()

        }
    }

    private fun loadConfig() {
        config = ConfigService.loadConfig(extension, "levels.yml")
    }

    /**
     * 为一个玩家添加经验值
     * @param player 玩家
     * @param exp 经验值
     * @return 增加后的经验值
     */
    fun addExp(player: Player, exp: Long): Long {
        return 0
    }

    /**
     * 为玩家移除一些经验值
     * @param player 玩家
     * @param exp 经验值
     * @return 移除后的经验值
     */
    fun removeExp(player: Player, exp: Long): Long {
        return 0
    }

    /**
     * 为玩家添加等级
     * @param player 玩家
     * @param level 等级
     * @return 移除玩家的等级
     */
    fun addLevel(player: Player, level: Long): Long {
        return 0
    }

    /**
     * 为玩家移除一些等级
     * @param player 玩家
     * @param level 等级
     * @return 移除后的玩家等级
     */
    fun removeLevel(player: Player, level: Long): Long {
        return 0
    }
}