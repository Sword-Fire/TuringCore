package net.geekmc.turingcore.command.basic

import net.geekmc.turingcore.command.args
import net.geekmc.turingcore.command.findPlayers
import net.geekmc.turingcore.command.setDefaultValueToSelf
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.help
import net.geekmc.turingcore.util.extender.onlyOp
import net.geekmc.turingcore.util.lang.Lang
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kommand

@Suppress("SpellCheckingInspection")
@AutoRegister
object CommandGamemode : Kommand({

    val di = turingCoreDi
    val lang: Lang by di.instance()

    val targetArg = ArgumentEntity("target").onlyPlayers(true).setDefaultValueToSelf()
    val modeArgs = ArgumentWord("mode").from(
        *listOf(
            GameMode.values().map { it.toString() },
            GameMode.values().map { it.toString().lowercase() },
            listOf("0", "1", "2", "3")
        ).flatten().toTypedArray()
    )

    help {
        sender.sendLang(lang, "cmd.gamemode.help")
    }

    fun setGameMode(sender: CommandSender, player: Player, mode: String) {
        player.gameMode = when (mode.uppercase()) {
            "0", "SURVIVAL" -> GameMode.SURVIVAL
            "1", "CREATIVE" -> GameMode.CREATIVE
            "2", "ADVENTURE" -> GameMode.ADVENTURE
            "3", "SPECTATOR" -> GameMode.SPECTATOR
            else -> error("unreachable.")
        }
        sender.sendLang(lang, "cmd.gamemode.succ", player.username, player.gameMode.name)
    }
//    syntax(modeArgs) {
//        if (sender !is Player) {
//            sender.sendLang(lang, "global.cmd.playerOnly")
//            return@syntax
//        }
//        setGameMode(player, player, !modeArgs)
//    }.onlyOp()

    syntax(modeArgs, targetArg) {
        val players = (!targetArg).findPlayers(sender)
        if (players.isEmpty()) {
            sender.sendLang(lang, "global.cmd.playerNotFound", args.getRaw(targetArg))
            return@syntax
        }
        players.forEach {
            setGameMode(sender, it, !modeArgs)
        }
    }.onlyOp()
}, name = "gamemode", aliases = arrayOf("gm"))