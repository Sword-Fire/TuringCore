package net.geekmc.turingcore.service.coin

import net.minestom.server.command.CommandSender
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.kommand.Kommand
import net.geekmc.turingcore.db.db
import net.geekmc.turingcore.db.entity.CoinType
import net.geekmc.turingcore.db.table.coinTypes
import net.geekmc.turingcore.db.table.playerUuids
import net.geekmc.turingcore.db.util.getCoinAmountOrCreate
import net.geekmc.turingcore.util.onlyOp
import net.minestom.server.command.builder.arguments.ArgumentString
import net.minestom.server.command.builder.arguments.number.ArgumentInteger
import net.minestom.server.entity.Player
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.single
import org.ktorm.entity.toList
import world.cepi.kstom.command.arguments.ArgumentPlayer

object CommandCoin : Kommand({
    val add by literal
    val remove by literal
    val set by literal

    val show by literal

    val help by literal

    fun showHelp(sender: CommandSender) {
        // TODO: i18n(sendLang)
        sender.sendMessage("Usage: add|remove|set <amount> <player>")
        sender.sendMessage("Usage: show [id|name] <player>")
        sender.sendMessage("Usage: help")
    }

    syntax {
        showHelp(sender)
    }

    subcommand("add") {
        val amount = ArgumentInteger("amount")
        // [ArgumentPlayer] 只能用来指定在线玩家
        val player = ArgumentString("player")

        syntax(amount, player) {
            val targetAmount = !amount
            val targetPlayer = !player

        }.onlyOp()
    }

    subcommand("remove") {
        // TODO: Check perms
    }

    subcommand("set") {
        // TODO: Check perms
    }

    subcommand("show") {
        // TODO: impl suggest
        val idOrName = ArgumentString("idOrName")

        fun showCoin(player: Player, coinType: CoinType) {
            val playerUuid = db.playerUuids.single { it.uuid eq player.uuid }
            getCoinAmountOrCreate(playerUuid, coinType).let {
                // TODO: i18n(sendLang)
                player.sendMessage("CoinType: ${it.type.name}")
                player.sendMessage("Amount: ${it.amount}")
            }
        }

        fun showCoin(player: Player, idOrName: String) {
            val coinType = db.coinTypes.find { it.id eq idOrName } ?: db.coinTypes.find { it.name eq idOrName }
            requireNotNull(coinType)
            showCoin(player, coinType)
        }

        fun findAllTypes(): List<CoinType> =
            db.coinTypes.toList()

        syntax {
            findAllTypes().forEach {
                showCoin(player, it)
            }
        }.onlyPlayers()

        syntax(idOrName) {
            showCoin(player, !idOrName)
        }.onlyPlayers()
    }

    subcommand("help") {
        syntax {
            showHelp(sender)
        }
    }
}, name = "coin")
