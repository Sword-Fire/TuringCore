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

    private val GLOBAL_LANG_FILE_PATH = Path.of("lang.yml")
    private val extension by instance<Extension>()
    private val dataPath by instance<Path>(tag = PathKeys.EXTENSION_FOLDER)
    val GLOBAL_LANG_PATH = dataPath.resolve(GLOBAL_LANG_FILE_PATH)

    private val langMap = mutableMapOf<String, Lang>()

    private fun init() {
        extension.saveResource(GLOBAL_LANG_FILE_PATH, GLOBAL_LANG_FILE_PATH, false)
    }

    fun getGlobalLang(): GlobalLang {
        return langMap.getOrPut("global") {
            GlobalLang()
        } as GlobalLang
    }

    fun getExtensionLang(extension: Extension): ExtensionLang {
        return langMap.getOrPut(extension.origin.name) {
            ExtensionLang(extension)
        } as ExtensionLang
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