package net.geekmc.turingcore.library.data.player

import net.minestom.server.event.Event
import java.util.*

/**
 * 玩家数据加载事件，用于插件预处理玩家数据，例如填充一些需要默认值但又无法在构造函数内初始化的字段，比如 HashMap 。
 * 可能发生于离线玩家，因此没有玩家实例。
 */
class PlayerDataLoadEvent(
    val uuid: UUID
) : Event {
    inline fun <reified T : PlayerData> getData(): T {
        return PlayerDataService.getPlayerData(uuid, T::class)
    }
}