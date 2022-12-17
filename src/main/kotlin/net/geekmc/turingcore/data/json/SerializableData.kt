package net.geekmc.turingcore.data.json

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.geekmc.turingcore.util.trace

/**
 * 数据类，能序列化为字符串/从字符串反序列化。
 * 支持存储的对象有kotlin serialization支持的所有对象，和[TURING_MODULE]支持的Minestom对象。
 */
open class SerializableData(input: String) {

    val writeData = LinkedHashMap<String, @Serializable Any>()
    val writeDataFunc = LinkedHashMap<String, () -> String>()
    val readData = LinkedHashMap<String, @Serializable Any>()

    val serializedData: LinkedHashMap<String, String>

    init {
        serializedData = TURING_JSON.decodeFromString(input)
    }

    companion object {

        const val EMPTY_FILE_STR = "{}"

        val emptyData: SerializableData
            get() = SerializableData(EMPTY_FILE_STR)

        fun load(str: String) = SerializableData(str)
    }

    inline operator fun <reified T : @Serializable Any> get(key: String): T? {
        when {
            writeData.contains(key) -> {
                trace("get $key from writeData.")
                return writeData[key] as T
            }

            readData.contains(key) -> {
                trace("get $key from readData")
                return readData[key] as T
            }

            serializedData.contains(key) -> {
                trace("get $key from serializedData")
                val value = TURING_JSON.decodeFromString<T>(serializedData[key]!!)
                readData[key] = value
                return value
            }
        }
        trace("get $key from null")
        return null
    }

    inline operator fun <reified T : @Serializable Any> get(key: String, def: T): T = get(key) ?: def

    inline operator fun <reified T : @Serializable Any> set(key: String, value: T) {
        writeData[key] = value
        writeDataFunc[key] = { TURING_JSON.encodeToString(value) }
    }

    fun saveToString(): String {
        writeData.keys.forEach {
            serializedData[it] = writeDataFunc[it]!!()
        }
        return TURING_JSON.encodeToString(serializedData)
    }
}