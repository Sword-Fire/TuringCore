package net.geekmc.turingcore.db

import net.geekmc.turingcore.TuringCore
import org.flywaydb.core.Flyway
import org.ktorm.database.Database

fun migrateDatabase() {
    val dbName = DbYamlConfig.INSTANCE.DB_PATH.toAbsolutePath().toString()
    // TODO: 避免硬编码字符串拼接
    Flyway.configure(TuringCore::class.java.classLoader).dataSource("jdbc:sqlite:${dbName}", null, null).load().migrate()
}

lateinit var db: Database
    private set

fun configDatabase() {
    val dbName = DbYamlConfig.INSTANCE.DB_PATH.toAbsolutePath().toString()
    // TODO: 连接池
    db = Database.connect("jdbc:sqlite:${dbName}", "org.sqlite.JDBC")
}