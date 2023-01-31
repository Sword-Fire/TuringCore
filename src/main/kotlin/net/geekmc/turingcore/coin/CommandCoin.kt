package net.geekmc.turingcore.coin


import net.geekmc.turingcore.util.extender.onlyOp
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.number.ArgumentLong
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.kommand.Kommand

object CommandCoin : Kommand({

    val help by literal

    commandHelpMessage = { sender ->
        sender.sendMessage("Usage: add|remove|set <amount> <coin> <player>")
        sender.sendMessage("Usage: show [id|name] <player>")
        sender.sendMessage("Usage: help")
    }

    val amountArg = ArgumentLong("amount")
    // Kstom 的 [world.cepi.kstom.command.arguments.suggest] 实现有问题，直接调用 MineStom 的 API
    val coinArg = ArgumentWord("coin")
//        .setSuggestionCallback{ _, _, suggestion ->
//        coinTypeRepo.findAllCoinTypes().getOrThrow().map { it.id }.map { SuggestionEntry(it) }
//            .forEach(suggestion::addEntry)
//    }
    // [ArgumentPlayer] 只能用来指定在线玩家，所以自己实现了一个
    // Kstom 的 [world.cepi.kstom.command.arguments.suggest] 实现有问题，直接调用 MineStom 的 API
    val playerArg = ArgumentWord("player")
//        .setSuggestionCallback { _, _, suggestion ->
//        playerUuidRepo.findAllPlayerUuids().getOrThrow().map { it.name }.map { SuggestionEntry(it) }
//            .forEach(suggestion::addEntry)
//    }

    subcommand("add") {
        syntax(amountArg, coinArg, playerArg) {
            val targetAmount = !amountArg
            val targetCoin = !coinArg
            val targetPlayer = !playerArg

            require(targetAmount > 0) { "Amount must be positive!" }

            // TODO: i18n(sendLang)
            sender.sendMessage("Success!")
        }.onlyOp()
    }

    subcommand("remove") {
        syntax(amountArg, coinArg, playerArg) {
            val targetAmount = !amountArg
            val targetCoin = !coinArg
            val targetPlayer = !playerArg

            require(targetAmount > 0) { "Amount must be positive!" }

            // 确保一致性，使用事务
//            db.useTransaction {
//                val playerUuidObj = playerUuidRepo.findPlayerUuidByName(targetPlayer).getOrThrow()
//                val coinTypeObj = coinTypeRepo.findCoinTypeById(targetCoin).getOrThrow()
//                val currentAmount =
//                    coinAmountRepo.findCoinAmountByUuidAndType(playerUuidObj.uuid, coinTypeObj.id).getOrThrow()
//                val beforeAmount = currentAmount.amount
//                currentAmount.amount -= targetAmount
//                coinAmountRepo.updateCoinAmount(currentAmount).getOrThrow()
//                coinHistoryRepo.addCoinHistory {
//                    player = playerUuidObj
//                    coinType = coinTypeObj
//                    actionType = CoinHistoryActionType.REMOVE
//                    before = beforeAmount
//                    amount = -targetAmount
//                    time = Instant.now()
//                    reason = "Remove Command, sender: ${sender.displayName}"
//                }.getOrThrow()
//            }
            // TODO: i18n(sendLang)
            sender.sendMessage("Success!")
        }.onlyOp()
    }

    subcommand("set") {
        syntax(amountArg, coinArg, playerArg) {
            val targetAmount = !amountArg
            val targetCoin = !coinArg
            val targetPlayer = !playerArg

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
            // TODO: i18n(sendLang)
            sender.sendMessage("Success!")
        }.onlyOp()
    }

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
