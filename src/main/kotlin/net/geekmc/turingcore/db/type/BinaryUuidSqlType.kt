package net.geekmc.turingcore.db.type

import net.geekmc.turingcore.util.extender.asBytes
import net.geekmc.turingcore.util.extender.asUuid
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.Types
import java.util.*

fun BaseTable<*>.binaryUuid(name: String): Column<UUID> {
    return registerColumn(name, BinaryUuidSqlType)
}

/**
 * 将 [UUID] 作为 BINARY 类型存储在数据库中
 *
 * 另外还有 [TextUuidSqlType]，将 [UUID] 作为 TEXT 类型存储在数据库中
 * 但由于性能问题不推荐使用 [TextUuidSqlType]
 * @see [TextUuidSqlType]
 */
object BinaryUuidSqlType : SqlType<UUID>(Types.BINARY, "bytes") {
    override fun doSetParameter(ps: java.sql.PreparedStatement, index: Int, parameter: UUID) {
        ps.setBytes(index, parameter.asBytes())
    }

    override fun doGetResult(rs: java.sql.ResultSet, index: Int): UUID {
        return rs.getBytes(index).asUuid()
    }
}