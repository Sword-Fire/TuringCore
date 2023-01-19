package net.geekmc.turingcore.data

import kotlinx.serialization.Serializable

/**
 * 玩家数据类必须继承[PlayerData]，且不允许有泛型参数。
 * 警告：使用前请参阅[PlayerDataService.getData]。
 */
@Serializable
abstract class PlayerData {
    // 表示当前的PlayerData实例是否可用（即未因玩家退出游戏而被弃用）。
    // TODO　实现available功能
    var available = true
        private set
}