package net.geekmc.turingcore.service.coin

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.geekmc.turingcore.TuringCore
import net.geekmc.turingcore.config.BaseYamlConfig
import net.geekmc.turingcore.config.BaseYamlObject
import net.geekmc.turingcore.config.yaml
import kotlin.io.path.inputStream

@Suppress("PropertyName", "PrivatePropertyName")
class CoinYamlConfig(override val yamlObj: CoinConfigYamlObject): BaseYamlConfig {
    val COIN_TYPES by yaml(CoinConfigYamlObject::coinTypes)

    companion object {
        const val FILE_NAME = "coin-config.yml"
        val INSTANCE by lazy { getConfig() }

        private fun getConfig(): CoinYamlConfig {
            val configYamlObj: CoinConfigYamlObject =
                Yaml.default.decodeFromStream(TuringCore.INSTANCE.dataDirectory.resolve(FILE_NAME).inputStream())
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
