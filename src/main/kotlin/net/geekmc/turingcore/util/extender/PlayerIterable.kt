package net.geekmc.turingcore.util.extender

import net.minestom.server.entity.Player

fun Iterable<Player>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "..."
): String {
    return joinToString(separator, prefix, postfix, limit, truncated) {
        it.username
    }
}