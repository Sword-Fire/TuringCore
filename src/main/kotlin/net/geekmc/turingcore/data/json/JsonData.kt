package net.geekmc.turingcore.data.json

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.*

/**
 * 存储序列化数据到 Json 的数据类型。
 * 可序列化对象包括任何带有 @Serializable 注解的对象，例如 Int, ArrayList<Int> 等。
 * 和所有在 TuringJson 中具有对应序列化器的对象，以及包含它们的具有 Kotlin 原生序列化支持的容器对象。例如 ItemStack, Set<ItemStack>。
 *
 * 你可以认为 [JsonData] 是一个简化版的 Map。
 * (String -> Serializable Object)
 *
 * 使用 [JsonData.load] 可以获取一个 JsonData。
 * 以及 JsonData 跟文件绑定，你可以使用 [JsonData.save] 方法将数据从内存存储进文件内。
 *
 * 使用范例：
 *
 * ```kotlin
 * // 这仅是一个例子，你可以使用 PlayerDataService 来对玩家数据进行操作。
 * // 当玩家进入服务器时：
 * val data = JsonData.load("PlayerData/${player.userName}.json")
 * player.money = data["money"] ?: 0
 * player.itemInMainHand = data.get<ItemStack>("mainHandItem", ItemStack(Material.AIR))
 *
 * // 当玩家退出服务器时：
 * data["money"] = player.money
 * data["mainHandItem"] = player.itemInMainHand
 * data.save()
 * ```
 */
class JsonData private constructor(private val file: Path) {

    companion object {
        const val EMPTY_FILE_CONTENT = "{}"

        // 该属性若迁移到TuringJson，则必须放在 TURING_CORE_SERIALIZATION_MODULE 之后，否则会报错。
        val SERIALIZATION_JSON = Json {
            serializersModule = TURING_CORE_SERIALIZATION_MODULE
            isLenient = true
            ignoreUnknownKeys = true
        }

        fun load(file: Path): JsonData {
            return JsonData(file)
        }
    }

    // JsonData由上至下分为三个数据层：writeData写入层，readData读取层，serializedData序列化层。
    // 读取数据时，依从上至下的顺序检测；若提供的key在该层有对应数据，则立即返回该数据。
    // 若读取数据时，readData层没有数据，则会将将serializedData层的数据反序列化写入readData层。
    // 写入数据时，只更改writeData层。
    // writeDataFunc用于存储包含泛型信息在内的信息，提供给序列化上两层的数据而后写入serializedData时使用。
    val writeData = LinkedHashMap<String, @Serializable Any>()
    val writeDataFunc = LinkedHashMap<String, () -> String>()
    val readData = LinkedHashMap<String, @Serializable Any>()
    val serializedData: LinkedHashMap<String, String>

    init {
        val fileContent = if (file.exists()) file.readText()
        else EMPTY_FILE_CONTENT
        serializedData = SERIALIZATION_JSON.decodeFromString(fileContent)
    }

    /**
     * 读取给定键的值，当值不存在时返回null。
     */
    inline operator fun <reified T : @Serializable Any> get(key: String): T? {
        return when {
            writeData.contains(key) -> writeData[key] as T
            readData.contains(key) -> readData[key] as T
            serializedData.contains(key) -> {
                val value = SERIALIZATION_JSON.decodeFromString<T>(serializedData[key]!!)
                readData[key] = value
                value
            }

            else -> null
        }
    }

    /**
     * 读取给定键的值，当值不存在时返回给定默认值。
     */
    inline operator fun <reified T : @Serializable Any> get(key: String, defaultValue: T): T = get(key) ?: defaultValue

    /**
     * 为给定键设置值。
     */
    inline operator fun <reified T : @Serializable Any> set(key: String, value: T) {
        writeData[key] = value
        writeDataFunc[key] = { SERIALIZATION_JSON.encodeToString(value) }
    }

    /**
     * 将数据保存到文件。
     */
    fun save() {
        // 将 writeData 层的数据写入 serializedData 层。
        writeData.keys.forEach {
            serializedData[it] = writeDataFunc[it]!!()
        }
        // 创建并写入文件。
        if (!file.exists()) {
            file.parent.createDirectories()
            file.createFile()
        }
        file.writeText(SERIALIZATION_JSON.encodeToString(serializedData))
    }
}