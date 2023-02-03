package net.geekmc.turingcore.util.lang

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.yamlMap
import net.geekmc.turingcore.library.di.PathKeys
import net.geekmc.turingcore.library.di.TuringCoreDIAware
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.service.Service
import net.geekmc.turingcore.util.extender.saveResource
import net.minestom.server.extensions.Extension
import org.kodein.di.instance
import org.slf4j.Logger
import java.nio.file.Path
import kotlin.io.path.inputStream

object LanguageService : Service(turingCoreDi), TuringCoreDIAware {
    private val logger by instance<Logger>()

    private val CONFIG_PATH = Path.of("lang.yml")
    private val extension by instance<Extension>()
    private val dataPath by instance<Path>(tag = PathKeys.EXTENSION_FOLDER)

    val messageMap = mutableMapOf<String, Message>()

    private fun init() {
        if (messageMap.isNotEmpty()) {
            messageMap.clear()
        }
        extension.saveResource(CONFIG_PATH, CONFIG_PATH, false)

        val configPath = dataPath.resolve(CONFIG_PATH)
        val root = Yaml.default.parseToYamlNode(configPath.inputStream())
        root.yamlMap.entries.forEach { (key, value) ->
            val keyStr = key.content
            val obj = if (value is YamlScalar) {
                MessageText(value.content)
            } else {
                when (val typeId = value.yamlMap.get<YamlScalar>("type")!!.content) {
                    "text" -> MessageText.create(value.yamlMap)
                    "actionbar" -> MessageActionBar.create(value.yamlMap)
                    "title" -> MessageTitle.create(value.yamlMap)

                    else -> error("Unknown message type: $typeId")
                }
            }
            messageMap[keyStr] = obj
            logger.info("Loaded message: $keyStr")
        }
    }

    override fun onEnable() {
        init()
    }
}