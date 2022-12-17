package net.geekmc.turingcore.data.json

import java.nio.file.Path
import kotlin.io.path.*

/**
 * 存储序列化数据到 Json 的数据类型。
 *
 * ```
 * 可序列化对象包括任何带有 @Serializable 注解的对象，例如 Int, ArrayList<Int> 等。
 * 和所有在 TuringJson 中具有对应序列化器的对象，以及包含它们的具有 Kotlin 原生序列化支持的容器对象。例如 ItemStack, Set<ItemStack>。
 * ```
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
class JsonData(private val file: Path) : SerializableData(kotlin.run {
    when (file.exists()) {
        true -> file.readText()
        else -> EMPTY_FILE_STR
    }
}) {

    companion object {

        fun load(file: Path): JsonData {
            return JsonData(file)
        }
    }

    fun save() {
        if (!file.exists()) {
            file.parent.createDirectories()
            file.createFile()
        }
        file.writeText(super.saveToString())
    }
}