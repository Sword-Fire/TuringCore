package net.geekmc.turingcore.command.debug

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.data.player.withOfflinePlayerData
import net.geekmc.turingcore.player.essentialdata.EssentialPlayerData
import net.geekmc.turingcore.player.uuid.UUIDService
import net.geekmc.turingcore.util.color.message
import net.minestom.server.entity.GameMode
import world.cepi.kstom.command.kommand.Kommand

object CommandTest : Kommand({

    playerCallbackFailMessage = {
        it.message("&r只有玩家能使用这个命令。")
    }

    opSyntax {
        withOfflinePlayerData(UUIDService.getUUID("Anzide")) {
            getData<EssentialPlayerData>().gameMode = GameMode.ADVENTURE
        }
    }

}, name = "test")