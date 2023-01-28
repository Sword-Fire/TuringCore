package net.geekmc.turingcore.config

import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.serializer
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.extender.saveResource
import net.minestom.server.extensions.Extension
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

/**
 * 全局数据服务。关闭后不允许再开启。
 */
object ConfigService : Service() {

    /**
     * 加载并获取配置文件。
     * @param resourcePath 文件在 jar 的 resource 文件夹下的相对资源路径。
     * @param targetPath 文件在 extension 文件夹下的相对目标路径。
     * @return 配置文件。
     */
    inline fun <reified T : Config> loadConfig(extension: Extension, resourcePath: String, targetPath: String = resourcePath): T {
        extension.saveResource(resourcePath, targetPath, false)
        val file = extension.dataDirectory.resolve(targetPath)
        // 文件不存在时写入默认值。
        val content = runBlocking {
            withContext(singleThreadContext) {
                file.readText()
            }
        }
        val data = yaml.decodeFromString<T>(content)
        clazzToFileMap[T::class] = file
        return data
    }

    // 用于读写的协程域。
    @OptIn(DelicateCoroutinesApi::class)
    val singleThreadContext = newSingleThreadContext("PlayerDataDispatcher")
    val clazzToFileMap = HashMap<KClass<out Config>, Path>()
    val yaml = Yaml.default

    internal fun saveConfig(config: Config) {
        val content = yaml.encodeToString(serializer(config.javaClass.kotlin.createType()), config)
        val file = clazzToFileMap[config::class]
        requireNotNull(file) { "YamlConfig ${config::class} is not registered yet!" }
        CoroutineScope(singleThreadContext).run {
            runCatching {
                file.writeText(content)
            }.onFailure {
                logger.warn("保存配置文件失败", it)
            }
        }
    }

}