package net.geekmc.turingcore.util.color

import net.geekmc.turingcore.data.yaml.YamlData
import net.geekmc.turingcore.di.PathKeys
import net.geekmc.turingcore.di.TuringCoreDIAware
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.extender.saveResource
import net.geekmc.turingcore.util.unsafeLazy
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.extensions.Extension
import org.kodein.di.instance
import java.nio.file.Path
import java.util.*

object ColorService : Service(), TuringCoreDIAware {

    private const val PATH = "colors.yml"
    private val extension by instance<Extension>()
    private val dataPath by instance<Path>(tag = PathKeys.EXTENSION_FOLDER)

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
        extension.saveResource(PATH)
        val data = YamlData(dataPath.resolve(PATH))
        val colors = data.getOrElse<List<String>>("colors") { emptyList() }
        colors
            .filter { it.isNotEmpty() && it.isNotBlank() }
            .forEach {
                val split = it.split("@")
                if (split.size != 2) {
                    logger.warn("无法解析颜色格式: $it")
                    return@forEach
                }
                colorMap[split[0]] = "<${split[1]}>"
            }
    }
}