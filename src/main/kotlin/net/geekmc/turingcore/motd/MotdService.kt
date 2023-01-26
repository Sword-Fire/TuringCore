package net.geekmc.turingcore.motd

import net.geekmc.turingcore.data.yaml.YamlData
import net.geekmc.turingcore.di.PathKey
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.color.toComponent
import net.geekmc.turingcore.util.extender.saveResource
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extensions.Extension
import net.minestom.server.ping.ResponseData
import org.kodein.di.instance
import world.cepi.kstom.event.listenOnly
import java.nio.file.Path
import java.util.*
import kotlin.io.path.notExists
import kotlin.io.path.readBytes

object MotdService : Service() {

    private const val ICON_PATH = "motd/icon.png"
    private const val MOTD_PATH = "motd/motd.yml"

    private lateinit var motdData: ResponseData
    private val extension by instance<Extension>()
    private val dataPath by instance<Path>(tag = PathKey.EXTENSION_FOLDER)

    override fun onEnable() {
        extension.saveResource(ICON_PATH)
        extension.saveResource(MOTD_PATH)
        val motdConfig = YamlData(dataPath.resolve(MOTD_PATH), MotdService.javaClass.classLoader)
        val descriptions: List<String> = motdConfig.getOrElse("description") { emptyList() }
        motdData = ResponseData().apply {
            // TODO: 是否应该硬编码两行
            description = "${descriptions[0]}\n${descriptions[1]}".toComponent()
            favicon = getIconAsBase64().getOrElse { "" }
        }
        EventNodes.DEFAULT.listenOnly<ServerListPingEvent> {
            responseData = motdData
        }
    }

    override fun onDisable() {}

    /**
     * 获取经由 Base64 编码的图标。
     */
    private fun getIconAsBase64(): Result<String> = runCatching {
        val path = dataPath.resolve(ICON_PATH)
        if (path.notExists()) error("Data path not exists.")

        val base64 = path.readBytes().let { bytes ->
            Base64.getEncoder().encodeToString(bytes)
        }
        return@runCatching "data:image/png;base64,$base64"
    }
}