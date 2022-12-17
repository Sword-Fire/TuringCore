package net.geekmc.turingcore.command.basic

import net.geekmc.turingcore.command.args
import net.geekmc.turingcore.command.basic.CommandGamemode.setGameMode
import net.geekmc.turingcore.command.findPlayers
import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import world.cepi.kstom.command.kommand.Kommand

@Suppress("SpellCheckingInspection")
object CommandGamemode : Kommand({

    val targetArg = ArgumentEntity("target").onlyPlayers(true)

    val modeArgs = ArgumentWord("mode").from(*kotlin.run {
        mutableListOf<String>().apply {
            addAll(GameMode.values().map { it.toString() })
            addAll(GameMode.values().map { it.toString().lowercase() })
            addAll(listOf("0", "1", "2", "3"))
        }.toTypedArray()
    })

    opSyntax {
        sender.sendLang("message-command-wrong-usage")
    }

    opSyntax(modeArgs) {
        if (sender !is Player) {
            sender.sendLang("message-command-player-only")
            return@opSyntax
        }
        setGameMode(player, player, !modeArgs)
    }

    opSyntax(modeArgs, targetArg) {
        val players = (!targetArg).findPlayers(sender)
        if (players.isEmpty()) {
            sender.sendLang("message-command-player-cannot-found", args.getRaw(targetArg))
            return@opSyntax
        }
        players.forEach {
            setGameMode(sender, it, !modeArgs)
        }
    }
}, name = "gamemode", aliases = arrayOf("gm", "gmode")) {

    fun setGameMode(sender: CommandSender, player: Player, mode: String) {
        player.gameMode = when (mode.uppercase()) {
            "0", "SURVIVAL" -> GameMode.SURVIVAL
            "1", "CREATIVE" -> GameMode.CREATIVE
            "2", "ADVENTURE" -> GameMode.ADVENTURE
            "3", "SPECTATOR" -> GameMode.SPECTATOR
            else -> error("unreachable.")
        }
        sender.sendLang("message-command-gamemode-succ", player.username, player.gameMode.name)
    }
}