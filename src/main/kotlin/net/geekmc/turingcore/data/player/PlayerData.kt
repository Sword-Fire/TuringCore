package net.geekmc.turingcore.data.player

import kotlinx.serialization.Serializable

/**
 * 玩家数据类必须继承[PlayerData]，且不允许有泛型参数。
 * 警告：使用前请参阅[PlayerDataService.getDataOfPlayer]。
 */
@Serializable
abstract class PlayerData {
    // TODO: 通过注解与alias指定序列化器而不是通过@Contexual
    // 表示当前的PlayerData实例是否可用。玩家退出游戏会导致此值变为false。在此之后，该PlayerData实例不应再被使用。
    var available = true
        internal set
}