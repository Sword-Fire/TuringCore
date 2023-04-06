package net.geekmc.turingcore.level

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

    // 缓存玩家数据
    private val players = HashMap<Player, LevelData>()
    private lateinit var config: LevelConfig

    override fun onEnable() {
        loadConfig()
        EventNodes.VERY_HIGH.listenOnly<PlayerDataLoadEvent> {
            val data = getData<LevelData>()
            // 缓存玩家数据
            Player.getEntity(uuid)?.let {
                players[it as Player] = data
            }
        }
    }

    private fun loadConfig() {
        config = ConfigService.loadConfig(extension, "levels.yml")
    }

    /**
     * 为一个玩家添加经验值
     * @param name 等级表名称
     * @param player 玩家
     * @param exp 经验值
     * @return 增加后的经验值
     */
    fun addExp(name: String, player: Player, exp: Long): Long {
        return 0
    }

    /**
     * 为玩家移除一些经验值
     * @param name 等级表名称
     * @param player 玩家
     * @param exp 经验值
     * @return 移除后的经验值
     */
    fun removeExp(name: String, player: Player, exp: Long): Long {
        return 0
    }

    /**
     * 为玩家添加等级
     * @param name 等级表名称
     * @param player 玩家
     * @param level 等级
     * @return 移除玩家的等级
     */
    fun addLevel(name: String, player: Player, level: Long): Long {
        return 0
    }

    /**
     * 为玩家移除一些等级
     * @param name 等级表名称
     * @param player 玩家
     * @param level 等级
     * @return 移除后的玩家等级
     */
    fun removeLevel(name: String, player: Player, level: Long): Long {
        return 0
    }

    /**
     * 设置玩家的等级
     * @param name 等级表名称
     * @param player 玩家
     * @return 玩家的等级
     */
    fun setLevel(name: String, player: Player, level: Long): Long {
        return 0
    }

    /**
     * 设置玩家的经验值
     * @param name 等级表名称
     * @param player 玩家
     * @param exp 经验值
     * @return 设置后的经验值
     */
    fun setExp(name: String, player: Player, exp: Long): Long {
        return 0
    }

    /**
     * 允许插件注册一个等级系统
     * @param name 等级系统名称
     * @param levels 等级与经验值的映射
     */
    fun register(name: String, levels: HashMap<Long, Long>) {

    }

    /**
     * 允许插件注销一个等级系统
     * @param type 等级系统名称
     */
    fun unregister(type: String) {

    }
}