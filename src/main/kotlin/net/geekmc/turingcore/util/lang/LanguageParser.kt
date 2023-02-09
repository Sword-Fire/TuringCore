package net.geekmc.turingcore.util.lang

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.yamlMap
import java.nio.file.Path
import kotlin.io.path.inputStream

object LanguageParser {
    fun parseLangYaml(yamlNode: YamlNode): Map<String, Message> {
        return yamlNode.yamlMap.entries.map { (key, value) ->
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

    fun parseLangYaml(langYaml: Path): Map<String, Message> =
        parseLangYaml(Yaml.default.parseToYamlNode(langYaml.inputStream()))

    fun parseLangYaml(langText: String): Map<String, Message> =
        parseLangYaml(Yaml.default.parseToYamlNode(langText))
}