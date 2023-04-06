package net.geekmc.turingcore.level

import net.geekmc.turingcore.level.event.PlayerExpChangedEvent
import net.geekmc.turingcore.library.config.ConfigService
import net.geekmc.turingcore.library.data.player.PlayerDataLoadEvent
import net.geekmc.turingcore.library.data.player.PlayerDataService
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.event.EventNodes
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.library.service.Service
import net.minestom.server.entity.Player
import net.minestom.server.event.EventDispatcher
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
            val data = getData<LevelData>()
            for ((type, _) in config.levels) {
                if (!data.levels.contains(type)) {
                    data.levels[type] = Level(0, 0)
                }
            }
        }
    }

    private fun loadConfig() {
        config = ConfigService.loadConfig(extension, "levels.yml")
    }

    @SuppressWarnings("all")
    fun isLevelExist(type: String): Boolean {
        return config.levels.containsKey(type)
    }

    /**
     * 为一个玩家添加经验值
     * @param type 等级表名称
     * @param player 玩家
     * @param exp 经验值
     * @return 增加后的经验值
     */
    fun addExp(type: String, player: Player, exp: Long): Long {
        require(isLevelExist(type)) { "Level type $type does not exist" }

        return 0
    }

    /**
     * 为玩家移除一些经验值
     * @param type 等级表名称
     * @param player 玩家
     * @param exp 经验值
     * @return 移除后的经验值
     */
    fun removeExp(type: String, player: Player, exp: Long): Long {
        require(isLevelExist(type)) { "Level type $type does not exist" }

        return 0
    }

    /**
     * 为玩家添加等级
     * @param type 等级表名称
     * @param player 玩家
     * @param level 等级
     * @return 移除玩家的等级
     */
    fun addLevel(type: String, player: Player, level: Long): Long {
        require(isLevelExist(type)) { "Level type $type does not exist" }

        return 0
    }

    /**
     * 为玩家移除一些等级
     * @param type 等级表名称
     * @param player 玩家
     * @param level 等级
     * @return 移除后的玩家等级
     */
    fun removeLevel(type: String, player: Player, level: Long): Long {
        require(isLevelExist(type)) { "Level type $type does not exist" }

        return 0
    }

    /**
     * 设置玩家的等级
     * @param type 等级表名称
     * @param player 玩家
     * @return 玩家的等级
     */
    fun setLevel(type: String, player: Player, level: Long): Long {
        require(isLevelExist(type)) { "Level type $type does not exist" }

        val data: LevelData = PlayerDataService.getPlayerData(player.uuid, LevelData::class)
        val levelData = data.levels[type]!!

        val original = levelData.level
        levelData.level = level

        if (config.levels[type]!!.main) {
            player.level = levelData.level.toInt()
        }

        EventDispatcher.call(PlayerExpChangedEvent(player, type, original, levelData.level))

        return 0
    }

    /**
     * 设置玩家的经验值
     * @param type 等级表名称
     * @param player 玩家
     * @param exp 经验值
     * @return 设置后的经验值
     */
    fun setExp(type: String, player: Player, exp: Long): Long {
        require(isLevelExist(type)) { "Level type $type does not exist" }

        val data: LevelData = PlayerDataService.getPlayerData(player.uuid, LevelData::class)
        val level = data.levels[type]!!

        val original = level.exp
        level.exp = exp + level.exp

        if (config.levels[type]!!.main) {
            player.exp = level.exp.toFloat()
        }

        EventDispatcher.call(PlayerExpChangedEvent(player, type, original, level.exp))

        return 0
    }

    /**
     * 允许插件注册一个等级系统
     * @param type 等级系统名称
     * @param levels 等级与经验值的映射
     */
    @Deprecated("Use config instead")
    fun register(type: String, levels: HashMap<Long, Long>) {
    }

    /**
     * 允许插件注销一个等级系统
     * @param type 等级系统名称
     */
    @Deprecated("Use config instead")
    fun unregister(type: String) {
    }
}