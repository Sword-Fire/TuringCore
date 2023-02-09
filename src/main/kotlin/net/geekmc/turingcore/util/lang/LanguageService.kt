package net.geekmc.turingcore.util.lang

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.yamlMap
import net.geekmc.turingcore.library.di.PathKeys
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.service.Service
import net.geekmc.turingcore.util.extender.saveResource
import net.minestom.server.extensions.Extension
import org.kodein.di.instance
import java.nio.file.Path
import kotlin.io.path.inputStream

object LanguageService : Service(turingCoreDi) {

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
        parseLangYaml(configPath).forEach { (k, v) ->
            messageMap[k] = v
        }
    }

    fun parseLangYaml(langYaml: Path): Map<String, Message> {
        val root = Yaml.default.parseToYamlNode(langYaml.inputStream())
        return root.yamlMap.entries.map { (key, value) ->
            key.content to if (value is YamlScalar) {
                MessageText(value.content)
            } else {
                when (val typeId = value.yamlMap.get<YamlScalar>("type")!!.content) {
                    "text" -> MessageText.create(value.yamlMap)
                    "actionbar" -> MessageActionBar.create(value.yamlMap)
                    "title" -> MessageTitle.create(value.yamlMap)

                    else -> error("Unknown message type: $typeId")
                }
            }
        }.toMap()
    }

    override fun onEnable() {
        init()
    }
}