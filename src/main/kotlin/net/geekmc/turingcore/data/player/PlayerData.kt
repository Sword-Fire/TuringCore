package net.geekmc.turingcore.data.player

import kotlinx.serialization.Serializable

/**
 * 玩家数据类必须继承 [PlayerData] ，主构造函数所有参数必须为具有默认值的属性，且不允许有泛型参数。
 * 警告：使用前请参阅 [getData] 。
 * @property available 表示当前实例是否可用，玩家退出游戏会导致此值变为 false 。在此之后，实例不应再被使用。
 */
@Serializable
abstract class PlayerData {
    var available = true
        internal set
}