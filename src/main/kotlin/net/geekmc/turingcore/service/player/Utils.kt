package net.geekmc.turingcore.service.player

import net.geekmc.turingcore.data.json.JsonData
import net.geekmc.turingcore.service.player.impl.PlayerBasicDataService
import net.minestom.server.entity.Player

val Player.data: JsonData
    get() = PlayerBasicDataService.service.getPlayerData(this)