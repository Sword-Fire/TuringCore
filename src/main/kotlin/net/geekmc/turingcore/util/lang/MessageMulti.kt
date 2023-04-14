package net.geekmc.turingcore.util.lang

import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minestom.server.command.CommandSender

@Serializable
@SerialName("multi")
data class MessageMulti(
    val msg: List<String>,
    val interval: Long
) : Message {
    companion object {
        fun create(yamlMap: YamlMap): MessageMulti =
            MessageMulti(
                yamlMap.get<YamlScalar>("msg")!!.content.split('\n').dropLast(1),
                yamlMap.get<YamlScalar>("interval")!!.toLong()
            )
    }

    override fun send(sender: CommandSender, vararg args: Any) {
        // TODO
    }
}
