package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.data.yaml.YamlData
import net.geekmc.turingcore.di.PathKeys
import net.geekmc.turingcore.di.TuringCoreDIAware
import net.geekmc.turingcore.util.extender.saveResource
import net.minestom.server.extensions.Extension
import org.kodein.di.instance
import java.nio.file.Path

object LanguageUtil : TuringCoreDIAware {

    private const val CONFIG_PATH = "lang.yml"
    private val extension by instance<Extension>()
    private val dataPath by instance<Path>(tag = PathKeys.EXTENSION_FOLDER)

    private val languageType = hashMapOf(
        "text" to TypeText::class.java,
        "actionbar" to TypeActionBar::class.java,
        "title" to TypeTitle::class.java
    )

    val messageMap = mutableMapOf<String, Type>()

    fun init() {
        if (messageMap.isNotEmpty()) {
            messageMap.clear()
        }
        extension.saveResource(CONFIG_PATH, CONFIG_PATH, false)
        val data = YamlData(dataPath.resolve(CONFIG_PATH))
        data.rootMapObject.forEach { (k, v) ->
            if (k !is String) return
            messageMap[k] = when (v) {
                is String -> {
                    TypeText(v)
                }

                is List<*> -> TypeText().apply {
                    v.mapNotNull { sub ->
                        if (sub is Map<*, *>) {
                            val source = sub.map { it.key.toString() to it.value!! }.toMap()
                            val type = languageType[source["type"]]?.getDeclaredConstructor()?.newInstance()
                                ?: error("Mismatched language node: ${source["type"]}. ($k)")
                            type.apply {
                                init(source)
                            }
                        } else {
                            init(mutableMapOf("texts" to v))
                        }
                    }
                }

                else -> error("Unsupported language node: $v. ($k)")
            }
        }
    }
}