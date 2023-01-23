package net.geekmc.turingcore.motd

import net.geekmc.turingcore.data.yaml.YamlData
import net.geekmc.turingcore.di.PathKey
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.color.toComponent
import net.geekmc.turingcore.util.saveResource
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extensions.Extension
import net.minestom.server.ping.ResponseData
import org.kodein.di.instance
import world.cepi.kstom.event.listenOnly
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists

object MotdService : Service() {

    private const val ICON_PATH = "motd/icon.png"
    private const val MOTD_PATH = "motd/motd.yml"

    private lateinit var motdData: ResponseData
    private val extension by instance<Extension>()
    private val dataPath by instance<Path>(tag = PathKey.DATA_FOLDER)

    override fun onEnable() {
        extension.saveResource(ICON_PATH)
        extension.saveResource(MOTD_PATH)
        val motdConfig = YamlData(dataPath.resolve(MOTD_PATH), MotdService.javaClass.classLoader)
        val descriptions = motdConfig.getOrElse<List<String>>("description") { emptyList() }
        motdData = ResponseData().apply {
            description = (descriptions[0] + "\n" + descriptions[1]).toComponent()
            favicon = getIconAsBase64() ?: return@apply
        }
        EventNodes.DEFAULT.listenOnly<ServerListPingEvent> {
            responseData = motdData
        }
    }

    override fun onDisable() {}

    /**
     * 获取经由 Base64 编码的图标。
     */
    private fun getIconAsBase64(): String? {
        val path = dataPath.resolve(ICON_PATH)
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