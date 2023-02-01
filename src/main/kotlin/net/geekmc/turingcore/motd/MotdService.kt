package net.geekmc.turingcore.motd

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.color.toComponent
import net.geekmc.turingcore.library.config.Config
import net.geekmc.turingcore.library.config.ConfigService
import net.geekmc.turingcore.library.di.PathKeys
import net.geekmc.turingcore.library.event.EventNodes
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.library.service.Service
import net.geekmc.turingcore.util.extender.saveResource
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extensions.Extension
import net.minestom.server.ping.ResponseData
import org.kodein.di.instance
import world.cepi.kstom.event.listenOnly
import java.nio.file.Path
import java.util.*
import kotlin.io.path.div
import kotlin.io.path.fileSize
import kotlin.io.path.notExists
import kotlin.io.path.readBytes

@AutoRegister
object MotdService : Service() {

    private const val ICON_PATH = "motd/icon.png"
    private const val CONFIG_PATH = "motd/config.yml"

    private val extension by instance<Extension>()
    private val configService by instance<ConfigService>()
    private val dataPath by instance<Path>(tag = PathKeys.EXTENSION_FOLDER)

    lateinit var config: MotdConfig

    override fun onEnable() {
        extension.saveResource(ICON_PATH, ICON_PATH, false)
        config = configService.loadConfig(extension, CONFIG_PATH)
        require(config.description.size <= 2) { "Motd description must have at most two lines." }

        val motdData = ResponseData().apply {
            description = config.description.joinToString("\n").toComponent()
            favicon = getIconAsBase64().getOrElse {
                logger.warn("Failed to load icon. Use empty.", it)
                ""
            }
        }
        EventNodes.DEFAULT.listenOnly<ServerListPingEvent> {
            responseData = motdData
        }
    }

    /**
     * 获取经由 Base64 编码的图标。
     */
    private fun getIconAsBase64(): Result<String> = runCatching {
        val path = dataPath / ICON_PATH
        if (path.notExists()) error("Icon file not exists.")

        if (path.fileSize() > 64 * 1024.0)
            logger.warn("Icon size is larger than 64KB. Watch out for performance issues.")

        val base64 = path.readBytes().let { bytes ->
            Base64.getEncoder().encodeToString(bytes)
        }
        return@runCatching "data:image/png;base64,$base64"
    }

    @Serializable
    data class MotdConfig(
        val description: ArrayList<String> = arrayListOf("A Minecraft Server", "Powered by TuringCore"),
    ) : Config()
}