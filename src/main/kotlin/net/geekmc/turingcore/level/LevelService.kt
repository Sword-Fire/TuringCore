package net.geekmc.turingcore.level

import net.geekmc.turingcore.level.event.PlayerExpChangedEvent
import net.geekmc.turingcore.level.event.PlayerLevelChangedEvent
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

/**
 * 等级系统服务
 *
 * 当经验值出现变化后触发 [PlayerExpChangedEvent]
 * 当等级出现变化后触发 [PlayerLevelChangedEvent]
 */
@AutoRegister
@Suppress("unused")
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
    fun addExp(type: String, player: Player, exp: Int): Int {
        require(isLevelExist(type)) { "Level type $type does not exist" }
        require(exp < 0) { "The target value $exp cannot be less than 0." }

        val data: LevelData = PlayerDataService.getPlayerData(player.uuid, LevelData::class)
        val level = data.levels[type]!!

        val original = level.exp

        // 检查是否超过最大经验值
        require(level.exp + exp > config.levels[type]!!.maxExp) {
            "The target value $exp cannot be greater than ${config.levels[type]!!.maxExp}."
        }

        level.exp = level.exp + exp

        // 处理升降级
        calculateLevel(type, data)

        if (config.levels[type]!!.main && config.levels[type]!!.display) {
            player.exp = level.exp.toFloat()
            player.level = level.level
        }

        EventDispatcher.call(PlayerExpChangedEvent(player, type, original, level.exp))

        return 0
    }

    /**
     * 为玩家移除一些经验值
     * @param type 等级表名称
     * @param player 玩家
     * @param exp 经验值
     * @return 移除后的经验值
     */
    fun removeExp(type: String, player: Player, exp: Int): Int {
        require(isLevelExist(type)) { "Level type $type does not exist" }
        require(exp < 0) { "The target value $exp cannot be less than 0." }

        val data: LevelData = PlayerDataService.getPlayerData(player.uuid, LevelData::class)
        val level = data.levels[type]!!

        val original = level.exp

        // 检查是否小于 0
        require(level.exp - exp < 0) { "The target value $exp cannot be less than 0." }

        level.exp = level.exp - exp

        // 处理升降级
        calculateLevel(type, data)

        if (config.levels[type]!!.main && config.levels[type]!!.display) {
            player.exp = level.exp.toFloat()
            player.level = level.level
        }

        EventDispatcher.call(PlayerExpChangedEvent(player, type, original, level.exp))

        return 0
    }

    /**
     * 为玩家添加等级
     * @param type 等级表名称
     * @param player 玩家
     * @param level 等级
     * @return 移除玩家的等级
     */
    fun addLevel(type: String, player: Player, level: Int): Int {
        require(isLevelExist(type)) { "Level type $type does not exist" }
        require(level < 0) { "The target value $level cannot be less than 0." }

        val data: LevelData = PlayerDataService.getPlayerData(player.uuid, LevelData::class)
        val levelData = data.levels[type]!!

        val original = levelData.level

        // 检查是否超过最大等级
        require(levelData.level + level > config.levels[type]!!.maxLevel) {
            "The target value $level cannot be greater than ${config.levels[type]!!.maxLevel}."
        }

        levelData.level = levelData.level + level

        calculateExp(type, data)

        if (config.levels[type]!!.main && config.levels[type]!!.display) {
            player.exp = levelData.exp.toFloat()
            player.level = levelData.level
        }

        EventDispatcher.call(PlayerLevelChangedEvent(player, type, original, levelData.level))

        return 0
    }

    /**
     * 为玩家移除一些等级
     * @param type 等级表名称
     * @param player 玩家
     * @param level 等级
     * @return 移除后的玩家等级
     */
    fun removeLevel(type: String, player: Player, level: Int): Int {
        require(isLevelExist(type)) { "Level type $type does not exist" }
        require(level < 0) { "The target value $level cannot be less than 0." }

        val data: LevelData = PlayerDataService.getPlayerData(player.uuid, LevelData::class)
        val levelData = data.levels[type]!!

        val original = levelData.level

        // 检查是否小于 0
        require(levelData.level - level < 0) { "The target value $level cannot be less than 0." }

        levelData.level = levelData.level - level

        calculateExp(type, data)

        if (config.levels[type]!!.main && config.levels[type]!!.display) {
            player.exp = levelData.exp.toFloat()
            player.level = levelData.level
        }

        EventDispatcher.call(PlayerLevelChangedEvent(player, type, original, levelData.level))

        return 0
    }

    /**
     * 设置玩家的等级
     * @param type 等级表名称
     * @param player 玩家
     * @return 玩家的等级
     */
    fun setLevel(type: String, player: Player, level: Int): Int {
        require(isLevelExist(type)) { "Level type $type does not exist" }
        require(level < 0) { "The target value $level cannot be less than 0." }
        require(level > config.levels[type]!!.maxLevel) {
            "The target value $level cannot be greater than ${config.levels[type]!!.maxLevel}."
        }

        val data: LevelData = PlayerDataService.getPlayerData(player.uuid, LevelData::class)
        val levelData = data.levels[type]!!

        val original = levelData.level
        levelData.level = level

        // 处理经验值
        calculateExp(type, data)

        if (config.levels[type]!!.main && config.levels[type]!!.display) {
            player.exp = levelData.exp.toFloat()
            player.level = levelData.level
        }

        EventDispatcher.call(PlayerLevelChangedEvent(player, type, original, levelData.level))

        return 0
    }

    /**
     * 设置玩家的经验值
     * @param type 等级表名称
     * @param player 玩家
     * @param exp 经验值
     * @return 设置后的经验值
     */
    fun setExp(type: String, player: Player, exp: Int): Int {
        require(isLevelExist(type)) { "Level type $type does not exist" }
        require(exp < 0) { "The target value $exp cannot be less than 0." }
        require(exp > config.levels[type]!!.maxExp) {
            "The target value $exp cannot be greater than ${config.levels[type]!!.maxExp}."
        }

        val data: LevelData = PlayerDataService.getPlayerData(player.uuid, LevelData::class)
        val level = data.levels[type]!!

        val original = level.exp
        level.exp = exp

        // 处理升降级
        calculateLevel(type, data)

        if (config.levels[type]!!.main && config.levels[type]!!.display) {
            player.exp = level.exp.toFloat()
            player.level = level.level
        }

        EventDispatcher.call(PlayerExpChangedEvent(player, type, original, level.exp))

        return 0
    }

    /**
     * 计算正确的经验值
     * @param data 玩家等级数据
     */
    private fun calculateExp(type: String, data: LevelData) {
        val levelData = data.levels[type]!!

        if (config.levels[type]!!.type == LevelType.SHADOW) {
            // 计算 level 处于等级映射表中的哪一个部分 并给予经验值
            val levelMap = config.levels[type]!!.shadow
            val levelMapKeys = levelMap.keys
            val levelMapKeysList = levelMapKeys.toList()
            val levelMapKeysListSize = levelMapKeysList.size
            for (i in 0 until levelMapKeysListSize) {
                if (levelMapKeysList[i] > levelData.level) {
                    if (i == 0) {
                        levelData.exp = 0
                    } else {
                        levelData.exp = levelMap[levelMapKeysList[i - 1]]!!
                    }
                    break
                }
            }
        } else {
            // TODO: 使用公式计算经验值
        }
    }

    private fun calculateLevel(type: String, data: LevelData) {
        val levelData = data.levels[type]!!

        if (config.levels[type]!!.type == LevelType.SHADOW) {
            // 计算经验值在那一部分给予正确的等级
            val levelMap = config.levels[type]!!.shadow
            val levelMapValues = levelMap.values
            val levelMapValuesList = levelMapValues.toList()
            val levelMapValuesListSize = levelMapValuesList.size
            for (i in 0 until levelMapValuesListSize) {
                if (levelMapValuesList[i] > levelData.exp) {
                    if (i == 0) {
                        levelData.level = 0
                    } else {
                        levelData.level = levelMap.keys.toList()[i - 1]
                    }
                    break
                }
            }
        } else {
            // TODO: 使用公式计算等级
        }
    }

    /**
     * 允许插件注册一个等级系统
     * @param type 等级系统名称
     * @param levels 等级与经验值的映射
     */
    @Deprecated("Use config instead")
    fun register(type: String, levels: HashMap<Int, Int>) {
    }

    /**
     * 允许插件注销一个等级系统
     * @param type 等级系统名称
     */
    @Deprecated("Use config instead")
    fun unregister(type: String) {
    }
}