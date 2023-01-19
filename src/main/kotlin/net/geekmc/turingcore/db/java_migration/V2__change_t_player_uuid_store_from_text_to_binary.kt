package net.geekmc.turingcore.db.java_migration

import net.geekmc.turingcore.util.extender.asBytes
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import java.util.*

/**
 * 将 t_player_uuid 表中的 player_uuid 字段从 text 类型改为 binary 类型
 */
@Suppress("ClassName", "SqlResolve")
object V2__change_t_player_uuid_store_from_text_to_binary : BaseJavaMigration() {
    override fun migrate(context: Context) {
        context.connection.let {
            // 新建临时表
            it.prepareStatement(
                """
                create table t_player_uuid_dg_tmp
                (
                    player_uuid BLOB not null
                        primary key
                        unique,
                    player_name TEXT not null
                        unique
                );""".trimIndent()
            ).execute()
            // 获取所有玩家 UUID 与 玩家名
            it.prepareStatement(
                """
                select player_uuid, player_name from t_player_uuid;
                """.trimIndent()
            ).executeQuery().let { results ->
                while (results.next()) {
                    // 将玩家 UUID 与 玩家名 添加进临时表
                    it.prepareStatement(
                        """
                        insert into t_player_uuid_dg_tmp (player_uuid, player_name) values (?, ?);
                        """.trimIndent()
                    ).apply {
                        val uuid = UUID.fromString(results.getString("player_uuid"))
                        val name = results.getString("player_name")
                        setBytes(1, uuid.asBytes())
                        setString(2, name)
                    }.execute()
                }
            }
            // 删除原表
            it.prepareStatement(
                """
                drop table t_player_uuid;
                """.trimIndent()
            ).execute()
            // 将临时表改名为原表名
            it.prepareStatement(
                """
                alter table t_player_uuid_dg_tmp rename to t_player_uuid;
                """.trimIndent()
            ).execute()
        }
    }
}