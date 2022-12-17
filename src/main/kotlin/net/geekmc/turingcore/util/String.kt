package net.geekmc.turingcore.util

fun String.replaceWithOrder(vararg args: Any): String {
    if (args.isEmpty() || isEmpty()) {
        return this
    }
    val chars = toCharArray()
    val builder = StringBuilder(length)
    var i = 0
    while (i < chars.size) {
        val mark = i
        if (chars[i] == '{') {
            var num = 0
            val alias = StringBuilder()
            while (i + 1 < chars.size && chars[i + 1] != '}') {
                i++
                if (Character.isDigit(chars[i]) && alias.isEmpty()) {
                    num *= 10
                    num += chars[i] - '0'
                } else {
                    alias.append(chars[i])
                }
            }
            if (i != mark && i + 1 < chars.size && chars[i + 1] == '}') {
                i++
                if (alias.isNotEmpty()) {
                    val str = alias.toString()
                    builder.append((args.firstOrNull { it is Pair<*, *> && it.second == str } as? Pair<*, *>)?.first ?: "{$str}")
                } else {
                    builder.append(args.getOrNull(num) ?: "{$num}")
                }
            } else {
                i = mark
            }
        }
        if (mark == i) {
            builder.append(chars[i])
        }
        i++
    }
    return builder.toString()
}