package net.geekmc.turingcore.service.impl.motd

import net.geekmc.turingcore.TuringCore
import net.geekmc.turingcore.data.yaml.YamlData
import net.geekmc.turingcore.service.MinestomService
import net.geekmc.turingcore.util.color.toComponent
import net.geekmc.turingcore.util.resolvePath
import net.geekmc.turingcore.util.saveResource
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.ping.ResponseData
import world.cepi.kstom.event.listenOnly
import java.io.IOException
import java.nio.file.Files
import java.util.*
import kotlin.io.path.exists

object MotdService : MinestomService() {

    private const val ICON_PATH = "motd/icon.png"
    private const val MOTD_PATH = "motd/motd.yml"

    private lateinit var motdData: ResponseData

    override fun onEnable() {
        TuringCore.INSTANCE.saveResource(ICON_PATH)
        TuringCore.INSTANCE.saveResource(MOTD_PATH)
        val motdConfig = YamlData(TuringCore.INSTANCE.resolvePath(MOTD_PATH), MotdService.javaClass.classLoader)
        val descriptions = motdConfig.get<List<String>>("description", emptyList())
        motdData = ResponseData().apply {
            description = (descriptions[0] + "\n" + descriptions[1]).toComponent()
            favicon = getIconAsBase64() ?: return@apply
        }
        eventNode.listenOnly<ServerListPingEvent> {
            responseData = motdData
        }
    }

    override fun onDisable() {}

    /**
     * 获取经由 Base64 编码的图标。
     */
    private fun getIconAsBase64(): String? {
        val path = TuringCore.INSTANCE.resolvePath(ICON_PATH)
        if (!path.exists()) return null
        try {
            val bytes = Files.readAllBytes(path)
            return "data:image/png;base64,${Base64.getEncoder().encodeToString(bytes)}"
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}