package net.geekmc.turingcore.util.lang

import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.geekmc.turingcore.library.color.toComponent
import net.geekmc.turingcore.util.extender.replaceWithOrder
import net.minestom.server.command.CommandSender

@Serializable
@SerialName("actionbar")
data class MessageActionBar(
    val msg: String,
    val duration: Long
) : Message {
    companion object {
        fun create(yamlMap: YamlMap): MessageActionBar =
            MessageActionBar(
                yamlMap.get<YamlScalar>("msg")!!.content,
                yamlMap.get<YamlScalar>("duration")!!.content.toLong()
            )
    }

    override fun send(sender: CommandSender, vararg args: Any) {
        // TODO: add duration
        sender.sendActionBar(msg.replaceWithOrder(args).toComponent())
    }
}