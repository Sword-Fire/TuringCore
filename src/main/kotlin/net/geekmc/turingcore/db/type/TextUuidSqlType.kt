package net.geekmc.turingcore.db.type

import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.Types
import java.util.*

fun BaseTable<*>.textUuid(name: String): Column<UUID> {
    return registerColumn(name, TextUuidSqlType)
}

object TextUuidSqlType : SqlType<UUID>(Types.LONGVARCHAR, typeName = "TEXT") {
    override fun doSetParameter(ps: java.sql.PreparedStatement, index: Int, parameter: UUID) {
        ps.setString(index, parameter.toString())
    }

    override fun doGetResult(rs: java.sql.ResultSet, index: Int): UUID {
        return UUID.fromString(rs.getString(index))
    }
}