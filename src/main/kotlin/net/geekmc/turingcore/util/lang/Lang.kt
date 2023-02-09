package net.geekmc.turingcore.util.lang

import net.minestom.server.extensions.Extension
import java.nio.file.Path

const val GLOBAL_PREFIX = "global."

sealed class Lang {
    abstract var messages: Map<String, Message>
    abstract val namespace: String

    abstract fun reload()
    abstract operator fun get(key: String): Message?
}

class GlobalLang internal constructor() : Lang() {
    override var messages = LanguageParser.parseLangYaml(LanguageService.GLOBAL_LANG_PATH)
    override val namespace = "global"

    override fun reload() {
        messages = LanguageParser.parseLangYaml(LanguageService.GLOBAL_LANG_PATH)
            .onEach { (t, _) ->
                check(t.startsWith("global-") || t.startsWith(GLOBAL_PREFIX)) {
                    "The key of global lang must start with `$GLOBAL_PREFIX`."
                }
            }
    }

    override operator fun get(key: String): Message? = messages[key]
}

class ExtensionLang internal constructor(extension: Extension) : Lang() {
    override var messages = emptyMap<String, Message>()
    override val namespace = extension.origin.name

    private val loadPaths = mutableListOf<Path>()

    private val globalLang = LanguageService.getGlobalLang()

    fun addLoadPath(vararg path: Path, reload: Boolean = true) {
        loadPaths.addAll(path)
        if (reload) reload()
    }

    override fun reload() {
        messages = loadPaths.map {
            LanguageParser.parseLangYaml(it)
        }.reduce(Map<String, Message>::plus)
    }

    override operator fun get(key: String): Message? {
        return if (key.startsWith(GLOBAL_PREFIX)) {
            globalLang[key]
        } else {
            messages[key]
        }
    }
}