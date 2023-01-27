@file:Suppress("RemoveExplicitTypeArguments")

package net.geekmc.turingcore.di

import net.minestom.server.MinecraftServer
import net.minestom.server.advancements.AdvancementManager
import net.minestom.server.adventure.bossbar.BossBarManager
import net.minestom.server.command.CommandManager
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.exception.ExceptionManager
import net.minestom.server.extensions.Extension
import net.minestom.server.extensions.ExtensionManager
import net.minestom.server.instance.InstanceManager
import net.minestom.server.instance.block.BlockManager
import net.minestom.server.listener.manager.PacketListenerManager
import net.minestom.server.monitoring.BenchmarkManager
import net.minestom.server.network.ConnectionManager
import net.minestom.server.recipe.RecipeManager
import net.minestom.server.scoreboard.TeamManager
import net.minestom.server.timer.SchedulerManager
import net.minestom.server.world.DimensionTypeManager
import net.minestom.server.world.biomes.BiomeManager
import org.kodein.di.DI
import org.kodein.di.LateInitDI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.slf4j.Logger
import java.nio.file.Path

val turingCoreDi: LateInitDI = LateInitDI()

fun initTuringCoreDi(extension: Extension) {
    turingCoreDi.baseDI = DI {
        bindSingleton<Extension> { extension }
        importAll(
            baseModule,
            managerModule
        )
    }
}

enum class PathKeys {
    EXTENSION_FOLDER,
}

val baseModule by DI.Module {
    bindSingleton<Logger> { instance<Extension>().logger }
    bindSingleton<Path>(tag = PathKeys.EXTENSION_FOLDER) { instance<Extension>().dataDirectory }
}

val managerModule by DI.Module {
    bindSingleton<AdvancementManager> { MinecraftServer.getAdvancementManager() }
    bindSingleton<BenchmarkManager> { MinecraftServer.getBenchmarkManager() }
    bindSingleton<BiomeManager> { MinecraftServer.getBiomeManager() }
    bindSingleton<BossBarManager> { MinecraftServer.getBossBarManager() }
    bindSingleton<BlockManager> { MinecraftServer.getBlockManager() }
    bindSingleton<CommandManager> { MinecraftServer.getCommandManager() }
    bindSingleton<ConnectionManager> { MinecraftServer.getConnectionManager() }
    bindSingleton<DimensionTypeManager> { MinecraftServer.getDimensionTypeManager() }
    bindSingleton<ExceptionManager> { MinecraftServer.getExceptionManager() }
    bindSingleton<ExtensionManager> { MinecraftServer.getExtensionManager() }
    bindSingleton<GlobalEventHandler> { MinecraftServer.getGlobalEventHandler() }
    bindSingleton<InstanceManager> { MinecraftServer.getInstanceManager() }
    bindSingleton<PacketListenerManager> { MinecraftServer.getPacketListenerManager() }
    bindSingleton<RecipeManager> { MinecraftServer.getRecipeManager() }
    bindSingleton<SchedulerManager> { MinecraftServer.getSchedulerManager() }
    bindSingleton<TeamManager> { MinecraftServer.getTeamManager() }
}
