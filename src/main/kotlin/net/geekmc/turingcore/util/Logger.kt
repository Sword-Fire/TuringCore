package net.geekmc.turingcore.util

import org.slf4j.Logger

inline fun Logger.info(message: () -> Any) {
    if (this.isInfoEnabled)
        info(message().toString())
}

inline fun Logger.trace(message: () -> Any) {
    if (this.isTraceEnabled)
        trace(message().toString())
}

inline fun Logger.debug(message: () -> Any) {
    if (this.isDebugEnabled)
        debug(message().toString())
}

inline fun Logger.warn(message: () -> Any) {
    if (this.isWarnEnabled)
        warn(message().toString())
}

inline fun Logger.error(message: () -> Any) {
    if (this.isErrorEnabled)
        error(message().toString())
}