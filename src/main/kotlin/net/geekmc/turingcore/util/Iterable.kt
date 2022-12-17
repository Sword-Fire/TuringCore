package net.geekmc.turingcore.util

import net.minestom.server.entity.Player

inline fun <T> Iterable<T>.foldToString(filter: String, toString: (element: T) -> String): String {
    var firstOne = true
    val builder = StringBuilder("")
    for (element in this) {
        if (firstOne) {
            firstOne = false
            builder.append(toString(element))
        } else {
            builder.append(filter)
            builder.append(toString(element))
        }
    }
    return builder.toString()
}

fun Iterable<Player>.foldToString(): String {
    return foldToString(",") {
        it.username
    }
}