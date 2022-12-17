package net.geekmc.turingcore.util.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import net.minestom.server.MinecraftServer
import net.minestom.server.ServerProcess
import net.minestom.server.timer.ExecutionType
import kotlin.coroutines.CoroutineContext


@Suppress("UnstableApiUsage")
val Dispatchers.MinestomAsync: CoroutineDispatcher get() = AsyncCoroutineDispatcher(MinecraftServer.process())

@Suppress("UnstableApiUsage")
internal class AsyncCoroutineDispatcher(private val serverProcess: ServerProcess) : CoroutineDispatcher() {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!serverProcess.isAlive) {
            return
        }
        serverProcess.scheduler().scheduleNextProcess(block, ExecutionType.ASYNC)
    }
}