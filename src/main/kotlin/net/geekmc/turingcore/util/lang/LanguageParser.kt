package net.geekmc.turingcore.util.lang

import com.charleskorn.kaml.*
import java.nio.file.Path
import kotlin.io.path.inputStream

object LanguageParser {
    fun parseLangYaml(yamlNode: YamlNode): Map<String, Message> {
        return yamlNode.yamlMap.entries.map { (key, value) ->
            key.content to when (value) {
                is YamlNull -> MessageNull()
                is YamlScalar -> MessageText(value.content)
                is YamlMap -> when (val typeId = value.get<YamlScalar>("type")!!.content) {
                    "text" -> MessageText.create(value)
                    "null" -> MessageNull.create(value)
                    "actionbar" -> MessageActionBar.create(value)
                    "title" -> MessageTitle.create(value)
                    "multi" -> MessageMulti.create(value)

                    else -> error("Unknown message type: $typeId")
                }

                else -> error("Unknown Yaml format: $value")
            }
        }.toMap()
    }

    fun parseLangYaml(langYaml: Path): Map<String, Message> =
        parseLangYaml(Yaml.default.parseToYamlNode(langYaml.inputStream()))

    fun parseLangYaml(langText: String): Map<String, Message> =
        parseLangYaml(Yaml.default.parseToYamlNode(langText))
}