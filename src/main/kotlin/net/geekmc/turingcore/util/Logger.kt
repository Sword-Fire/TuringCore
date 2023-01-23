package net.geekmc.turingcore.util

import org.slf4j.Logger

inline fun Logger.info(message: () -> Any) {
    info(message().toString())
}

inline fun Logger.trace(message: () -> Any) {
    trace(message().toString())
}

inline fun Logger.debug(message: () -> Any) {
    debug(message().toString())
}

inline fun Logger.warn(message: () -> Any) {
    warn(message().toString())
}