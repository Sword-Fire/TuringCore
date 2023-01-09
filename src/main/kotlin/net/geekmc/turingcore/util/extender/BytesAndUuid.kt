package net.geekmc.turingcore.util.extender

import java.nio.ByteBuffer
import java.util.*

internal fun UUID.asBytes(): ByteArray =
    ByteBuffer.wrap(ByteArray(16)).apply {
        putLong(mostSignificantBits)
        putLong(leastSignificantBits)
    }.array()

internal fun ByteArray.asUuid(): UUID {
    require(size == 16) { "Invalid UUID byte array size: $size" }
    ByteBuffer.wrap(this).apply {
        val high = getLong(0)
        val low = getLong(1)
        return UUID(high, low)
    }
}