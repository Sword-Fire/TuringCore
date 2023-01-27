package net.geekmc.turingcore.config

import kotlinx.coroutines.*
import net.geekmc.turingcore.service.Service
import java.util.*
import kotlin.io.path.*

/**
 * 全局数据服务。关闭后不允许再开启。
 */
object YamlConfigService : Service() {

    /**
     * 注册并获取全局数据。
     * @param path 文件在 extension 文件夹下的子路径。
     */
//    inline fun <reified T : YamlConfig> getConfig(extension: Extension, subPath: String): T {
//        val file = extension.dataDirectory.resolve(subPath)
//        val content = file.readText()
//        val data = SERIALIZATION_JSON.decodeFromString<T>(content)
//        dataSet[file] = data
//        return data
//    }
//
//    fun saveData() {
//        dataSet.forEach {
//            val file = it.key
//            val data = it.value
//            file.writeText(
//                SERIALIZATION_JSON.encodeToString(
//                    serializer(data.javaClass.kotlin.createType()),
//                    data
//                )
//            )
//        }
//    }
//
//    @OptIn(ExperimentalTime::class)
//    override fun onEnable() {
//
//        dataFolder.createDirectories()
//
//        // 定时保存，延时30分钟后进行第一次检查，并以后每30分钟检查一次
//        val saveInterval = Duration.ofMinutes(30)
//        Manager.scheduler.buildTask {
//            // 切到主线程
//            val time = measureTime {
//                saveData()
//            }.inWholeMilliseconds
//            logger.info("定时保存全局数据，耗时 $time ms")
//        }.delay(saveInterval).repeat(saveInterval).schedule()
//    }

}