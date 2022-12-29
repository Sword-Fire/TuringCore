package net.geekmc.turingcore.util

import net.minestom.server.entity.Entity

fun Entity.getLineOfSightEntity(range: Double): Entity? {
    return getLineOfSightEntity(range) { true }
}