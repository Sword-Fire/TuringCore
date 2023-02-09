package net.geekmc.turingcore.util.lang

import net.geekmc.turingcore.library.di.PathTags
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.service.Service
import net.geekmc.turingcore.util.extender.saveResource
import net.minestom.server.extensions.Extension
import org.kodein.di.instance
import java.nio.file.Path

object LanguageService : Service(turingCoreDi) {

    private val GLOBAL_LANG_FILE_PATH = Path.of("global-lang.yml")
    private val extension by instance<Extension>()
    private val dataPath by instance<Path>(tag = PathTags.EXTENSION_FOLDER)
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

    override fun onEnable() {
        init()
    }
}