package net.geekmc.turingcore.coin

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.geekmc.turingcore.config.BaseYamlConfig
import net.geekmc.turingcore.config.BaseYamlObject
import net.geekmc.turingcore.config.yaml
import java.nio.file.Path
import kotlin.io.path.inputStream

@Suppress("PropertyName")
class CoinYamlConfig(override val yamlObj: CoinConfigYamlObject): BaseYamlConfig {
    val COIN_TYPES by yaml(CoinConfigYamlObject::coinTypes)

    companion object {
        const val FILE_NAME = "coin-config.yml"

        fun getInstance(dataDirectory: Path): CoinYamlConfig {
            val configYamlObj: CoinConfigYamlObject =
                Yaml.default.decodeFromStream(dataDirectory.resolve(FILE_NAME).inputStream())
            return CoinYamlConfig(configYamlObj)
        }
    }
}

@Serializable
data class CoinConfigItem(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String
)

@Serializable
data class CoinConfigYamlObject(
    @SerialName("coin-types")
    val coinTypes: List<CoinConfigItem>
): BaseYamlObject