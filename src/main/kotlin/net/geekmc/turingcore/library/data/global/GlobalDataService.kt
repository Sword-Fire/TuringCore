package net.geekmc.turingcore.library.data.global

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.geekmc.turingcore.library.data.serialization.addMinestomSerializers
import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.service.Service
import world.cepi.kstom.Manager
import java.nio.file.Path
import java.time.Duration
import java.util.*
import kotlin.io.path.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * 全局数据服务。关闭后不允许再开启。
 */
object GlobalDataService : Service(turingCoreDi) {

    /**
     * 注册一个全局数据类。
     * @param T 全局数据类型
     * @param clazz 玩家数据类
     * @param identifier 数据标识符，会被用作存储文件命名。应当使用 [插件名:数据类名] 的形式 (e.g. TuringCore:WorldData)
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : GlobalData> register(clazz: KClass<T>, identifier: String): T {
        val fileName = "$identifier.json"
        val file = dataFolder.resolve(fileName)
        val content = if (file.exists()) file.readText() else "{}"
        val data = json.decodeFromString(serializer(clazz.createType()), content) as? T ?: error("Failed to decode global data ${clazz.qualifiedName}")
        fileToDataMap[file] = data
        return data
    }

    private val fileToDataMap = HashMap<Path, GlobalData>()

    @Suppress("spellCheckingInspection")
    private val dataFolder: Path = Path.of("globaldata")

    @OptIn(DelicateCoroutinesApi::class)
    private val singleThreadContext = newSingleThreadContext("PlayerDataDispatcher")

    private val json = Json {
        serializersModule = SerializersModule {
            addMinestomSerializers(this)
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun onEnable() {
        dataFolder.createDirectories()

        // 定时保存，延时30分钟后进行第一次检查，并以后每30分钟检查一次
        val saveInterval = Duration.ofMinutes(30)
        Manager.scheduler.buildTask {
            // 切到主线程
            val time = measureTime {
                saveData()
            }.inWholeMilliseconds
            serviceLogger.info("定时保存全局数据，耗时 $time ms")
        }.delay(saveInterval).repeat(saveInterval).schedule()

    }

    override fun onDisable() {
        saveData()
    }

    /**
     * 保存数据，在主线程序列化数据并异步写入文件。
     */
    private fun saveData() {
        fileToDataMap.forEach {
            val file = it.key
            val tempFile = file.resolveSibling(file.fileName.toString() + ".tmp")
            val data = it.value
            val content = json.encodeToString(serializer(data.javaClass.kotlin.createType()), data)
            CoroutineScope(singleThreadContext).launch {
                if (!tempFile.exists()) {
                    tempFile.parent.createDirectories()
                    tempFile.createFile()
                }
                tempFile.writeText(content)
                tempFile.moveTo(file, overwrite = true)
            }
        }
    }

}