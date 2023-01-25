package net.geekmc.turingcore.util.extender

import net.minestom.server.entity.Entity

fun Entity.getLineOfSightEntity(range: Double): Entity? {
    return this.getLineOfSightEntity(range) { true }
}