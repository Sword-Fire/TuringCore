package net.geekmc.turingcore.db

import net.geekmc.turingcore.TuringCore
import net.geekmc.turingcore.db.java_migration.javaBasedMigrations
import net.geekmc.turingcore.util.info
import org.flywaydb.core.Flyway
import org.ktorm.database.Database

fun migrateDatabase() {
    val dbName = DbYamlConfig.INSTANCE.DB_PATH.toAbsolutePath().toString()
    // TODO: 避免硬编码字符串拼接
    Flyway.configure(TuringCore::class.java.classLoader).dataSource("jdbc:sqlite:${dbName}", null, null)
        .javaMigrations(*javaBasedMigrations).load().migrate()
}

lateinit var db: Database
    private set

fun configDatabase() {
    val dbName = DbYamlConfig.INSTANCE.DB_PATH.toAbsolutePath().toString()
    // TODO: 连接池
    db = Database.connect("jdbc:sqlite:${dbName}", "org.sqlite.JDBC")
    db.useConnection {
        it.createStatement().execute("PRAGMA foreign_keys = ON;")
        info { "Database Foreign Keys Enabled." }
    }
}