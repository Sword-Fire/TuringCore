package net.geekmc.turingcore.coin


import net.geekmc.turingcore.coin.CoinService.coins
import net.geekmc.turingcore.library.data.player.withOfflinePlayerData
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.onlyOp
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.number.ArgumentLong
import world.cepi.kstom.Manager
import world.cepi.kstom.command.arguments.suggest
import world.cepi.kstom.command.arguments.suggestAllPlayers
import world.cepi.kstom.command.kommand.Kommand

// TODO: Lang
@AutoRegister
object CommandCoin : Kommand({

    help { sender ->
        sender.sendMessage("错误的命令: ${context.input}")
        sender.sendMessage("Usage: coin help")
        sender.sendMessage("Usage: coin see [player] <coin>")
        sender.sendMessage("Usage: coin add|remove|set <player> <coin> <amount>")
    }

    val coinArg = ArgumentWord("coin").suggest {
        for (coin in CoinService.getCoins()) {
            it.add(coin)
        }
    }
    val playerArg = ArgumentWord("player").suggestAllPlayers()
    val amountArg = ArgumentLong("amount")

    subcommand("see") {

        syntax(playerArg, coinArg) {
            val coin = !coinArg
            if (!CoinService.isCoinExist(coin)) {
                sender.sendMessage("货币 $coin 不存在。")
                return@syntax
            }
            val player = Manager.connection.findPlayer(!playerArg)
            val amount = if (player != null) player.coins[coin]
            else withOfflinePlayerData(!playerArg) {
                getData<CoinData>().coins[coin]!!
            }.getOrElse {
                sender.sendMessage("&dr读取数据失败")
                return@syntax
            }
            sender.sendMessage("${!playerArg} 的 $coin 余额为 $amount")
        }

        syntax(coinArg) {
            val coin = !coinArg
            if (!CoinService.isCoinExist(coin)) {
                sender.sendMessage("货币 $coin 不存在。")
                return@syntax
            }
            sender.sendMessage("${player.username} 的 $coin 余额为 ${player.coins[coin]}")
        }.onlyPlayers()
    }

    subcommand("add") {
        syntax(playerArg, coinArg, amountArg) {
            val coin = !coinArg
            if (!CoinService.isCoinExist(coin)) {
                sender.sendMessage("货币 $coin 不存在。")
                return@syntax
            }
            val username = !playerArg
            val player = Manager.connection.findPlayer(username)
            val amount = !amountArg
            if (amount <= 0) {
                sender.sendMessage("&r数值必须为正数")
                return@syntax
            }
            val result = if (player != null) {
                player.coins[coin] += amount
                player.coins[coin]
            } else {
                withOfflinePlayerData(username) {
                    val coinMap = getData<CoinData>().coins
                    coinMap[coin] = coinMap[coin]!! + amount
                    coinMap[coin]!!
                }.onFailure {
                    sender.sendMessage("&dr读取离线玩家数据失败")
                    return@syntax
                }.getOrElse { 0 }
            }
            sender.sendMessage("&g已为 &y${username} &g添加 &w${amount} &y$coin, &g现有 &w${result} &y$coin")
        }.onlyOp()
    }

    subcommand("remove", "rem") {
        syntax(playerArg, coinArg, amountArg) {
            val coin = !coinArg
            if (!CoinService.isCoinExist(coin)) {
                sender.sendMessage("货币 $coin 不存在。")
                return@syntax
            }
            val username = !playerArg
            val player = Manager.connection.findPlayer(username)
            val amount = !amountArg
            if (amount <= 0) {
                sender.sendMessage("&r数值必须为正数")
                return@syntax
            }
            val result = if (player != null) {
                player.coins[coin] -= amount
                player.coins[coin]
            } else {
                withOfflinePlayerData(username) {
                    val coinMap = getData<CoinData>().coins
                    coinMap[coin] = coinMap[coin]!! - amount
                    coinMap[coin]!!
                }.onFailure {
                    sender.sendMessage("&dr读取离线玩家数据失败")
                    return@syntax
                }.getOrElse { 0 }
            }
            sender.sendMessage("&g已为 &y${username} &g移除 &w${amount} &y$coin, &g现有 &w${result} &y$coin")
        }.onlyOp()
    }

    subcommand("set") {
        syntax(playerArg, coinArg, amountArg) {
            val coin = !coinArg
            if (!CoinService.isCoinExist(coin)) {
                sender.sendMessage("货币 $coin 不存在。")
                return@syntax
            }
            val username = !playerArg
            val player = Manager.connection.findPlayer(username)
            val amount = !amountArg
            if (amount <= 0) {
                sender.sendMessage("&r数值必须为正数")
                return@syntax
            }
            if (player != null) {
                player.coins[coin] = amount
                player.coins[coin]
            } else {
                withOfflinePlayerData(username) {
                    getData<CoinData>().coins[coin] = amount
                }.onFailure {
                    sender.sendMessage("&dr读取离线玩家数据失败")
                    return@syntax
                }
            }
            sender.sendMessage("&g已为 &y${username} &g的 &y$coin &g设置为 &w${amount}")
        }.onlyOp()
    }

}, name = "coin")
