package net.geekmc.turingcore.util

import net.minestom.server.entity.Entity

fun Entity.getLineOfSightEntity(range: Double): Entity? {
    return this.getLineOfSightEntity(range) { true }
}