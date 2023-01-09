package net.geekmc.turingcore.db.type

import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.Types
import java.util.*

@Deprecated("由于性能问题，请考虑使用 binaryUuid 代替", ReplaceWith("binaryUuid(name)"))
fun BaseTable<*>.textUuid(name: String): Column<UUID> {
    @Suppress("DEPRECATION")
    return registerColumn(name, TextUuidSqlType)
}

/**
 * 将 [UUID] 作为 TEXT 类型存储在数据库中
 *
 * 另外还有 [BinaryUuidSqlType]，将 [UUID] 作为 BINARY 类型存储在数据库中
 * 由于性能问题，请考虑使用 [BinaryUuidSqlType] 代替
 * @see [BinaryUuidSqlType]
 */
@Deprecated("由于性能问题，请考虑使用 BinaryUuidSqlType 代替")
object TextUuidSqlType : SqlType<UUID>(Types.LONGVARCHAR, typeName = "TEXT") {
    override fun doSetParameter(ps: java.sql.PreparedStatement, index: Int, parameter: UUID) {
        ps.setString(index, parameter.toString())
    }

    override fun doGetResult(rs: java.sql.ResultSet, index: Int): UUID {
        return UUID.fromString(rs.getString(index))
    }
}