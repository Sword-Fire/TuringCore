package net.geekmc.turingcore.library.data.player

import net.minestom.server.entity.Player
import java.util.*

inline fun <reified T : PlayerData> Player.getData(): T {
    return PlayerDataService.getPlayerData(this.uuid, T::class)
}

/**
 * @return true 如果成功读取数据并执行操作；false 如果读取数据失败并忽略操作。
 */
fun withOfflinePlayerData(username: String, action: OfflinePlayerDataContext.() -> Unit): Boolean {
    return PlayerDataService.withOfflinePlayerData(username, action)
}

/**
 * @see withOfflinePlayerData
 */
fun withOfflinePlayerData(uuid: UUID, action: OfflinePlayerDataContext.() -> Unit): Boolean {
    return PlayerDataService.withOfflinePlayerData(uuid, action)
}

class OfflinePlayerDataContext(val uuid: UUID) {
    inline fun <reified T : PlayerData> getData(): T {
        return PlayerDataService.getPlayerData(uuid, T::class)
    }
}