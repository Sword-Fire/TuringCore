package net.geekmc.turingcore.data.player

import net.geekmc.turingcore.di.turingCoreDi
import net.minestom.server.entity.Player
import org.kodein.di.instance
import java.util.*
import kotlin.reflect.KClass

interface PlayerDataService {
    /**
     * 注册一个玩家数据类。
     * @param T 玩家数据类型
     */
    fun <T : PlayerData> register(clazz: KClass<T>)

    /**
     * 获取玩家的某一类型的数据。只允许在主线程中使用。
     * 警告：在你的代码段运行完毕、让出主线程控制权后，通过该方法获取的引用可能在任何时候失效。
     * 因此，当你在某一时刻获取了主线程控制权，且想要访问玩家数据，总是应该通过 [getPlayerData] 方法来获取新的引用，且不要存放这个引用。
     */
    fun <T : PlayerData> getPlayerData(uuid: UUID, clazz: KClass<out PlayerData>): T

    /**
     * 在主线程中调用离线玩家的数据。文件读取会阻塞主线程，因此较慢。在传入的代码块中，使用 [getData][OfflinePlayerDataContext.getData] 获取数据。
     * @return 是否成功读取并执行代码块。
     */
    fun withOfflinePlayerData(uuid: UUID, action: OfflinePlayerDataContext.() -> Unit): Boolean

    fun withOfflinePlayerData(username: String, action: OfflinePlayerDataContext.() -> Unit): Boolean
}

inline fun <reified T : PlayerData> PlayerDataService.register() = register(T::class)

inline fun <reified T : PlayerData> Player.getData(): T {
    return playerDataService.getPlayerData(this.uuid, T::class)
}

fun withOfflinePlayerData(username: String, action: OfflinePlayerDataContext.() -> Unit): Boolean {
    return playerDataService.withOfflinePlayerData(username, action)
}

fun withOfflinePlayerData(uuid: UUID, action: OfflinePlayerDataContext.() -> Unit): Boolean {
    return playerDataService.withOfflinePlayerData(uuid, action)
}

class OfflinePlayerDataContext(val uuid: UUID) {
    inline fun <reified T : PlayerData> getData(): T {
        return PlayerDataServiceImpl.getPlayerData(uuid, T::class)
    }
}

val di = turingCoreDi
val playerDataService: PlayerDataService by di.instance()