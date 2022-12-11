package net.geekmc.turingcore.command

//import net.geekmc.turingcore.command.kommand.args
//import net.geekmc.turingcore.command.kommand.format
//import net.geekmc.turingcore.data.PlayerBasicDataService.data
import net.geekmc.turingcore.color.send
import net.geekmc.turingcore.ktx.command.kommand.Kommand
import net.minestom.server.command.builder.arguments.ArgumentBoolean
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.geekmc.turingcore.ktx.command.arguments.defaultValue
import net.geekmc.turingcore.ktx.command.kommand.args
import net.geekmc.turingcore.ktx.command.kommand.format

// TODO: impl, KDoc
object OpCommand : Kommand({

    val targetArg = ArgumentEntity("target").onlyPlayers(true).singleEntity(true)
    val booleanArg = ArgumentBoolean("boolean").defaultValue(true)

    format {
        sender.send("&r命令用法不正确: /${context.input}")
        sender.send("&r正确用法: /op <Player> [Boolean]")
    }

    format(targetArg, booleanArg) {

        val player = (!targetArg).findFirstPlayer(sender)

        if (player == null) {
            sender.send("&r找不到玩家: ${args.getRaw(targetArg)}")
            return@format
        }
        // TODO: player.data["op"]
//        player.data["op"] = !booleanArg
        sender.send("&g已将玩家玩家 &y${player.username} &g的管理员权限设置为 &r${!booleanArg}")
    }

}, "op")