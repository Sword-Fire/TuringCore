package net.geekmc.turingcore.util

import net.geekmc.turingcore.TuringCore

fun String.info() = info(this)

fun String.trace() = trace(this)

fun String.debug() = debug(this)

fun String.warn() = warn(this)

fun info(vararg message: Any) {
    message.forEach { TuringCore.INSTANCE.logger.info(it.toString()) }
}

inline fun info(message: () -> Any) {
    info(message())
}

fun trace(vararg message: Any) {
    message.forEach { TuringCore.INSTANCE.logger.trace(it.toString()) }
}

inline fun trace(message: () -> Any) {
    trace(message())
}

fun debug(vararg message: Any) {
    message.forEach { TuringCore.INSTANCE.logger.debug(it.toString()) }
}

inline fun debug(message: () -> Any) {
    debug(message())
}

fun warn(vararg message: Any) {
    message.forEach { TuringCore.INSTANCE.logger.warn(it.toString()) }
}

inline fun warn(message: () -> Any) {
    warn(message())
}