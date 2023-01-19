package net.geekmc.turingcore.player

import net.geekmc.turingcore.data.json.JsonData
import net.geekmc.turingcore.player.deprecated.DeprecatedPlayerBasicDataService
import net.minestom.server.entity.Player

val Player.data: JsonData
    get() = DeprecatedPlayerBasicDataService.service.getPlayerData(this)