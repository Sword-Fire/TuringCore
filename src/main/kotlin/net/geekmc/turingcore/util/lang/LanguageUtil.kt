package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.TuringCore
import net.geekmc.turingcore.data.yaml.YamlData
import net.geekmc.turingcore.util.resolvePath
import net.geekmc.turingcore.util.saveResource

object LanguageUtil {

    private const val PATH = "lang.yml"

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
        TuringCore.INSTANCE.saveResource(PATH)
        val data = YamlData(TuringCore.INSTANCE.resolvePath(PATH))
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