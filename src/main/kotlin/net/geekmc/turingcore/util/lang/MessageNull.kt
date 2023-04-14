package net.geekmc.turingcore.util.lang

import com.charleskorn.kaml.YamlMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minestom.server.command.CommandSender

@Serializable
@SerialName("null")
data class MessageNull(
    // Kotlin requires at least one property in a data class
    val placeholder: Int = 0
) : Message {
    companion object {
        fun create(yamlMap: YamlMap): MessageNull = MessageNull()
    }

    override fun send(sender: CommandSender, vararg args: Any) {
        // Do nothing
    }
}
