package net.geekmc.turingcore.util.color

import kotlinx.serialization.Serializable
import net.geekmc.turingcore.config.Config
import net.geekmc.turingcore.config.ConfigService
import net.geekmc.turingcore.di.TuringCoreDIAware
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.unsafeLazy
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.extensions.Extension
import org.kodein.di.instance
import java.util.*

object ColorService : Service(), TuringCoreDIAware {

    private const val CONFIG_PATH = "colors.yml"

    private val extension by instance<Extension>()
    private val configService by instance<ConfigService>()
    lateinit var config: ColorServiceConfig

    val miniMessage by unsafeLazy {
        MiniMessage.miniMessage()
    }

    /**
     * 存储自定义简写，并按照简写的长度从大到小排序。
     */
    val colorMap = TreeMap<String, String> { x, y ->
        if (x.length != y.length) {
            return@TreeMap -x.length.compareTo(y.length) // * - 1
        }
        return@TreeMap x.compareTo(y)
    }

    override fun onEnable() {

        config = configService.loadConfig(extension, CONFIG_PATH)
        for (format in config.colors) {
            val split = format.split("@")
            if (split.size != 2) {
                logger.warn("无法解析颜色格式: $format")
                continue
            }
            colorMap[split[0]] = "<${split[1]}>"
        }
    }

    @Serializable
    data class ColorServiceConfig(
        val colors: List<String> = emptyList()
    ) : Config()
}