package net.geekmc.turingcore.coin

import net.geekmc.turingcore.library.config.ConfigService
import net.geekmc.turingcore.library.data.player.PlayerDataLoadEvent
import net.geekmc.turingcore.library.data.player.getData
import net.geekmc.turingcore.library.di.TuringCoreDIAware
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.event.EventNodes
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.library.service.Service
import net.minestom.server.entity.Player
import net.minestom.server.extensions.Extension
import org.kodein.di.instance
import world.cepi.kstom.event.listenOnly

@AutoRegister
object CoinService : Service(turingCoreDi), TuringCoreDIAware {

    private val extension: Extension by instance()
    private lateinit var config: CoinConfig

    fun isCoinExist(type: String): Boolean {
        return config.coins.contains(type)
    }

    override fun onEnable() {
        loadConfig()
        // --- 加载时若某种货币的数据不存在，则初始化为0 ---
        EventNodes.VERY_HIGH.listenOnly<PlayerDataLoadEvent> {
            println("监听到 PlayerDataLoadEvent")
            val data = getData<CoinData>()
            for ((type, _) in config.coins) {
                if (!data.coins.contains(type)) {
                    data.coins[type] = 0
                }
            }
        }
    }

    private fun loadConfig() {
        config = ConfigService.loadConfig(extension, "coin.yml")
    }

    // --- 玩家货币操作 API ---

    val Player.coins: CoinMap
        get() = CoinMap(this.getData())

    /**
     * 该类在玩家离线后会失效，因此不要存储该对象的引用。
     */
    class CoinMap(val data: CoinData) {
        operator fun get(type: String): Long {
            require(isCoinExist(type)) { "Coin type $type does not exist" }
            return data.coins[type]!!
        }

        operator fun set(type: String, value: Long) {
            require(isCoinExist(type)) { "Coin type $type does not exist" }
            data.coins[type] = value
        }

        /**
         * 当玩家有足够货币时消耗货币并返回 true ，否则返回 false 。
         */
        fun checkThenTakeCoin(type: String, amount: Long): Boolean {
            data.apply {
                if (coins[type]!! >= amount) {
                    coins[type] = coins[type]!! - amount
                    return true
                }
            }
            return false
        }
    }

}