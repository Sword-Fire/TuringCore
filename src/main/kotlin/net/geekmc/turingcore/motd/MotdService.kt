package net.geekmc.turingcore.motd

import net.geekmc.turingcore.data.yaml.YamlData
import net.geekmc.turingcore.di.PathKeys
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.color.toComponent
import net.geekmc.turingcore.util.extender.div
import net.geekmc.turingcore.util.extender.saveResource
import net.geekmc.turingcore.util.extender.warn
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extensions.Extension
import net.minestom.server.ping.ResponseData
import org.kodein.di.instance
import world.cepi.kstom.event.listenOnly
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.fileSize
import kotlin.io.path.notExists
import kotlin.io.path.readBytes

object MotdService : Service() {
    private val MOTD_PATH = Path("motd")
    private val ICON_PATH = MOTD_PATH / "icon.png"
    private val CONFIG_PATH = MOTD_PATH / "config.yml"

    private val extension by instance<Extension>()
    private val dataPath by instance<Path>(tag = PathKeys.EXTENSION_FOLDER)

    override fun onEnable() {
        extension.saveResource(ICON_PATH)
        extension.saveResource(CONFIG_PATH)
        val motdConfig = YamlData(dataPath / CONFIG_PATH, MotdService.javaClass.classLoader)
        val descriptions: List<String> = motdConfig.getOrElse("description") { emptyList() }
        require(descriptions.size <= 2) { "Motd description must have at most two lines." }
        val motdData = ResponseData().apply {
            // TODO: 是否应该硬编码两行
            description = descriptions.joinToString("\n").toComponent()
            favicon = getIconAsBase64().getOrElse {
                logger.warn("Failed to load icon. Use empty.", it)
                ""
            }
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
        val path = dataPath / ICON_PATH
        if (path.notExists()) error("Icon path not exists.")

        if (path.fileSize() > 64 * 1024.0)
            logger.warn { "Icon size is larger than 64KB. Watch out for performance issues." }

        val base64 = path.readBytes().let { bytes ->
            Base64.getEncoder().encodeToString(bytes)
        }
        return@runCatching "data:image/png;base64,$base64"
    }
}