package net.geekmc.turingcore.data.global

import kotlinx.serialization.Serializable

/**
 * 全局数据类必须继承[GlobalData]，且不允许有泛型参数。
 */
@Serializable
abstract class GlobalData {
}