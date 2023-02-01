package net.geekmc.turingcore.library.framework

import net.geekmc.turingcore.library.data.global.GlobalData
import net.geekmc.turingcore.library.data.player.PlayerData

/**
 * 自动注册标记注解
 * @param id 注册使用的标识符。目前仅对 [PlayerData] 与 [GlobalData] 有效。可能添加对 [BlockHandler] 的支持。
 * @see [AutoRegisterFramework]
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class AutoRegister(
    val id: String = ""
)