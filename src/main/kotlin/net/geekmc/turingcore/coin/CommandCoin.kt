package net.geekmc.turingcore.coin


import net.geekmc.turingcore.coin.CoinService.coins
import net.geekmc.turingcore.library.data.player.withOfflinePlayerData
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.onlyOp
import net.geekmc.turingcore.util.extender.whenFalse
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.number.ArgumentLong
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import world.cepi.kstom.Manager
import world.cepi.kstom.command.kommand.Kommand

// TODO: Lang
@AutoRegister
object CommandCoin : Kommand({

    help { sender ->
        sender.sendMessage("Usage: add|remove|set <amount> <coin> <player>")
        sender.sendMessage("Usage: show [id|name] <player>")
        sender.sendMessage("Usage: help")
    }

    val amountArg = ArgumentLong("amount")

    val coinArg = ArgumentWord("coin")
    val playerArg = ArgumentWord("player").setSuggestionCallback { _, _, suggestion ->
        Manager.connection.onlinePlayers.forEach { suggestion.addEntry(SuggestionEntry(it.username)) }
    }

    // Kstom 的 [world.cepi.kstom.command.arguments.suggest] 实现有问题，直接调用 MineStom 的 API
//        .setSuggestionCallback { _, _, suggestion ->
//        playerUuidRepo.findAllPlayerUuids().getOrThrow().map { it.name }.map { SuggestionEntry(it) }
//            .forEach(suggestion::addEntry)
//    }

    // TODO: subcommand 失配的时候不会发送 commandHelpMessage
    subcommand("see") {

        help { sender ->
            sender.sendMessage("66666666666")
        }

        syntax(coinArg, playerArg) {
            println("test1")
            val coin = !coinArg
            if (!CoinService.isCoinExist(coin)) {
                sender.sendMessage("货币 $coin 不存在。")
                return@syntax
            }
            var amount: Long = 0
            val player = Manager.connection.findPlayer(!playerArg)
            if (player != null) {
                amount = player.coins[coin]
            } else {
                withOfflinePlayerData(!playerArg) {
                    amount = getData<CoinData>().coins[coin]!!
                }.whenFalse {
                    sender.sendMessage("&dr读取数据失败")
                    return@syntax
                }
            }
            sender.sendMessage("${!playerArg} 的 $coin 余额为 $amount")

        }.onlyOp()
    }

//    subcommand("add") {
//        syntax(amountArg, coinArg, playerArg) {
//            val targetAmount = !amountArg
//            val targetCoin = !coinArg
//            val targetPlayer = !playerArg
//
//            require(targetAmount > 0) { "Amount must be positive!" }
//
//            sender.sendMessage("Success!")
//        }.onlyOp()
//    }

//    subcommand("remove") {
//        syntax(amountArg, coinArg, playerArg) {
//            val targetAmount = !amountArg
//            val targetCoin = !coinArg
//            val targetPlayer = !playerArg
//
//            require(targetAmount > 0) { "Amount must be positive!" }
//            // TODO: i18n(sendLang)
//            sender.sendMessage("Success!")
//        }.onlyOp()
//    }

//    subcommand("set") {
//        syntax(amountArg, coinArg, playerArg) {
//            val targetAmount = !amountArg
//            val targetCoin = !coinArg
//            val targetPlayer = !playerArg

            // 确保一致性，使用事务
//            db.useTransaction {
//                val playerUuidObj = playerUuidRepo.findPlayerUuidByName(targetPlayer).getOrThrow()
//                val coinTypeObj = coinTypeRepo.findCoinTypeById(targetCoin).getOrThrow()
//                val currentAmount =
//                    coinAmountRepo.findCoinAmountByUuidAndType(playerUuidObj.uuid, coinTypeObj.id).getOrThrow()
//                val beforeAmount = currentAmount.amount
//                currentAmount.amount = targetAmount
//                coinAmountRepo.updateCoinAmount(currentAmount).getOrThrow()
//                coinHistoryRepo.addCoinHistory {
//                    player = playerUuidObj
//                    coinType = coinTypeObj
//                    actionType = CoinHistoryActionType.SET
//                    before = beforeAmount
//                    amount = targetAmount
//                    time = Instant.now()
//                    reason = "Set Command, sender: $sender"
//                }.getOrThrow()
//            }
//            // TODO: i18n(sendLang)
//            sender.sendMessage("Success!")
//        }.onlyOp()
//    }

//    subcommand("show") {
//        // TODO: impl suggest
//        val idOrName = ArgumentString("idOrName")
//
//        fun showCoin(player: Player, coinType: CoinType) {
//            coinAmountRepo.findCoinAmountByUuidAndType(player.uuid, coinType.id).getOrThrow().let {
//                // TODO: i18n(sendLang)
//                player.sendMessage("CoinType: ${it.type.name}")
//                player.sendMessage("Amount: ${it.amount}")
//            }
//        }
//
//        fun showCoin(player: Player, idOrName: String) {
//            // 先尝试按 id 查找，然后尝试按名称查找，最后判空
//            val coinTypes = requireNotNull((coinTypeRepo.findCoinTypeById(idOrName).getOrNull()?.run { listOf(this) }
//                ?: coinTypeRepo.findCoinTypesByName(idOrName).getOrNull())) { "CoinType $idOrName not found" }
//
//            coinTypes.forEach {
//                showCoin(player, it)
//            }
//        }
//
//        syntax {
//            coinTypeRepo.findAllCoinTypes().getOrThrow().forEach {
//                showCoin(player, it)
//            }
//        }.onlyPlayers()
//
//        syntax(idOrName) {
//            showCoin(player, !idOrName)
//        }.onlyPlayers()
//    }
}, name = "coin")
