package net.geekmc.turingcore.util.lang

import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minestom.server.command.CommandSender

@Serializable
@SerialName("title")
data class MessageTitle(
    val title: String,
    val subtitle: String,
    val fadein: Long,
    val duration: Long,
    val fadeout: Long
) : Message {
    companion object {
        fun create(yamlMap: YamlMap): MessageTitle =
            MessageTitle(
                yamlMap.get<YamlScalar>("title")!!.content,
                yamlMap.get<YamlScalar>("subtitle")!!.content,
                yamlMap.get<YamlScalar>("fadein")!!.content.toLong(),
                yamlMap.get<YamlScalar>("duration")!!.content.toLong(),
                yamlMap.get<YamlScalar>("fadeout")!!.content.toLong()
            )
    }

    override fun send(sender: CommandSender, vararg args: Any) {
        // TODO
//        val times = Title.Times.times(Duration.ofMillis(fadein), Duration.ofMillis(stay), Duration.ofMillis(fadeout))
//        val title = Title.title(title?.toComponent() ?: return, subtitle?.toComponent() ?: return, times)
//        sender.showTitle(title)
    }
}