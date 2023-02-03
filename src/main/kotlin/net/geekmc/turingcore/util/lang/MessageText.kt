package net.geekmc.turingcore.util.lang

import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.color.message
import net.geekmc.turingcore.util.extender.replaceWithOrder
import net.minestom.server.command.CommandSender

@Serializable
@SerialName("text")
data class MessageText(
    val msg: String
) : Message {
    companion object {
        fun create(yamlMap: YamlMap): MessageText =
            MessageText(yamlMap.get<YamlScalar>("msg")!!.content)
    }

    override fun send(sender: CommandSender, vararg args: Any) {
        sender.message(msg.replaceWithOrder(*args))
    }
}