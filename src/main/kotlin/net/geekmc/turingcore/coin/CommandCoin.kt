package net.geekmc.turingcore.coin

import net.geekmc.turingcore.coin.CoinService.coins
import net.geekmc.turingcore.library.data.player.withOfflinePlayerData
import net.geekmc.turingcore.library.di.TuringCoreDIAware
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.help
import net.geekmc.turingcore.util.extender.onlyOp
import net.geekmc.turingcore.util.lang.Lang
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.number.ArgumentLong
import net.minestom.server.entity.Player
import org.kodein.di.instance
import world.cepi.kstom.Manager
import world.cepi.kstom.command.arguments.defaultValue
import world.cepi.kstom.command.arguments.suggest
import world.cepi.kstom.command.arguments.suggestAllPlayers
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandCoin : Kommand({

    val coinArg = ArgumentWord("coin").suggest {
        for (coin in CoinService.getCoins()) {
            it.add(coin)
        }
    }
    val playerArgDefault = "\$self"
    val playerArg = ArgumentWord("player").suggestAllPlayers().defaultValue(playerArgDefault)
    val amountArg = ArgumentLong("amount")

    val di = turingCoreDi
    val lang: Lang by di.instance()

    help {
        sender.sendMessage("错误的命令: ${context.input}")
        sender.sendMessage("Usage: coin help")
        sender.sendMessage("Usage: coin see [player] <coin>")
        sender.sendMessage("Usage: coin add|remove|set <player> <coin> <amount>")
    }

    // FIXME: 当 coinArg 参数和 playerArg 参数顺序颠倒时，会导致 coinArg 参数无法正确解析
    // 以上为前提会导致输入指令 /coin see <coin> <player> 时，coin 和 player 都提示玩家名
    subcommand("see") {

        syntax(playerArg, coinArg) {
            val coin = !coinArg
            if (!CoinService.isCoinExist(coin)) {
                sender.sendLang(lang, "coin.cmd.notExist", coin)
                return@syntax
            }
            val username = if ((!playerArg) == playerArgDefault) {
                if (sender !is Player) {
                    sender.sendLang(lang, "global.cmd.playerOnly")
                    return@syntax
                }
                player.username
            } else !playerArg
            val player = Manager.connection.findPlayer(username)
            val amount = if (player != null) player.coins[coin]
            else withOfflinePlayerData(username) {
                getData<CoinData>().coins[coin]!!
            }.getOrElse {
                sender.sendLang(lang, "global.data.loadOfflineDataFailed")
                return@syntax
            }
            sender.sendLang(lang, "coin.cmd.see", username, coin, amount)
        }

    }

    subcommand("add") {
        syntax(playerArg, coinArg, amountArg) {
            val coin = !coinArg
            if (!CoinService.isCoinExist(coin)) {
                sender.sendLang(lang, "coin.cmd.notExist", coin)
                return@syntax
            }
            val username = if ((!playerArg) == playerArgDefault) {
                if (sender !is Player) {
                    sender.sendLang(lang, "global.cmd.playerOnly")
                    return@syntax
                }
                player.username
            } else !playerArg
            val player = Manager.connection.findPlayer(username)
            val amount = !amountArg
            if (amount <= 0) {
                sender.sendLang(lang, "coin.cmd.amountMustBePositive", amount)
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
                    sender.sendLang(lang, "global.data.loadOfflineDataFailed")
                    return@syntax
                }.getOrElse { 0 }
            }
            sender.sendLang(lang, "coin.cmd.add", username, amount, coin, result)
        }.onlyOp()
    }

    subcommand("remove", "rem") {
        syntax(playerArg, coinArg, amountArg) {
            val coin = !coinArg
            if (!CoinService.isCoinExist(coin)) {
                sender.sendLang(lang, "coin.cmd.notExist", coin)
                return@syntax
            }
            val username = if ((!playerArg) == playerArgDefault) {
                if (sender !is Player) {
                    sender.sendLang(lang, "global.cmd.playerOnly")
                    return@syntax
                }
                player.username
            } else !playerArg
            val player = Manager.connection.findPlayer(username)
            val amount = !amountArg
            if (amount <= 0) {
                sender.sendLang(lang, "coin.cmd.amountMustBePositive", amount)
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
                    sender.sendLang(lang, "global.data.loadOfflineDataFailed")
                    return@syntax
                }.getOrElse { 0 }
            }
            sender.sendLang(lang, "coin.cmd.remove", username, amount, coin, result)
        }.onlyOp()
    }

    subcommand("set") {
        syntax(playerArg, coinArg, amountArg) {
            val coin = !coinArg
            if (!CoinService.isCoinExist(coin)) {
                sender.sendLang(lang, "coin.cmd.notExist", coin)
                return@syntax
            }
            val username = if ((!playerArg) == playerArgDefault) {
                if (sender !is Player) {
                    sender.sendLang(lang, "global.cmd.playerOnly")
                    return@syntax
                }
                player.username
            } else !playerArg
            val player = Manager.connection.findPlayer(username)
            val amount = !amountArg
            if (amount <= 0) {
                sender.sendLang(lang, "coin.cmd.amountMustBePositive", amount)
                return@syntax
            }
            if (player != null) {
                player.coins[coin] = amount
                player.coins[coin]
                withOfflinePlayerData(username) {
                    getData<CoinData>().coins[coin] = amount
                }.onFailure {
                    sender.sendLang(lang, "global.data.loadOfflineDataFailed")
                    return@syntax
                }
            }
            sender.sendLang(lang, "coin.cmd.set", username, amount, coin)
        }.onlyOp()
    }

}, name = "coin"), TuringCoreDIAware
