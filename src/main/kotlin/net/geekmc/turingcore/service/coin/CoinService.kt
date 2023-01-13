package net.geekmc.turingcore.service.coin

import net.geekmc.turingcore.db.entity.CoinType
import net.geekmc.turingcore.db.table.coinTypes
import net.geekmc.turingcore.di.DITuringCoreAware
import net.geekmc.turingcore.service.MinestomService
import net.geekmc.turingcore.util.info
import org.kodein.di.instance
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.toList

object CoinService : MinestomService(), DITuringCoreAware {
    private val db by instance<Database>()
    private val coinYamlConfig by instance<CoinYamlConfig>()

    private fun refreshCoinTypes() {
        // TODO: 求求算法大佬救救孩子吧😭
        val coinTypes = coinYamlConfig.COIN_TYPES
        val coinTypesIds = coinTypes.map { it.id }
        val dbCoinTypes = db.coinTypes.toList()
        val dbCoinTypesIds = dbCoinTypes.map { it.id }
        coinTypes.forEach { coinType ->
            // 如果数据库中不存在该货币类型，则创建
            if (coinType.id !in dbCoinTypesIds) {
                db.coinTypes.add(CoinType {
                    this.id = coinType.id
                    this.name = coinType.name
                })
                info { "Added coin type ${coinType.id} to database." }
            // 否则按需更新货币类型
            } else {
                if (coinType.name != dbCoinTypes.find { it.id == coinType.id }?.name) {
                    db.coinTypes.find { it.id eq coinType.id }?.apply {
                        this.name = coinType.name
                        this.flushChanges()
                    }
                    info { "Updated coin type ${coinType.id} in database." }
                }
            }
        }
        dbCoinTypes.forEach { dbCoinType ->
            // 如果配置文件中不存在该货币类型，则删除
            if (dbCoinType.id !in coinTypesIds) {
                dbCoinType.delete()
                info { "Removed coin type ${dbCoinType.id} from database." }
            }
        }
    }

    override fun onEnable() {
        refreshCoinTypes()
        CommandCoin.register()
    }

    override fun onDisable() {
        error("CoinService cannot be disabled!")
    }
}