package net.geekmc.turingcore.db

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.geekmc.turingcore.config.BaseYamlConfig
import net.geekmc.turingcore.config.BaseYamlObject
import net.geekmc.turingcore.config.yaml
import java.nio.file.Path
import kotlin.io.path.inputStream

@Suppress("PropertyName", "PrivatePropertyName")
class DbYamlConfig(override val yamlObj: DbYamlObject, dataDirectory: Path) : BaseYamlConfig {
    private val _DB_NAME by yaml(DbYamlObject::dbName, "data.db")
    val DB_PATH: Path = dataDirectory.resolve(_DB_NAME)

    companion object {
        const val FILE_NAME = "db-config.yml"

        fun getInstance(dataDirectory: Path): DbYamlConfig {
            val configYamlObj: DbYamlObject = runCatching<DbYamlObject> {
                Yaml.default.decodeFromStream(dataDirectory.resolve(FILE_NAME).inputStream())
            }.getOrElse {
                DbYamlObject()
            }
            return DbYamlConfig(configYamlObj, dataDirectory)
        }
    }
}

@Serializable
data class DbYamlObject(
    @SerialName("db-name")
    val dbName: String? = null,
): BaseYamlObject