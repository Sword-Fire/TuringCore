package net.geekmc.turingcore.di

import net.minestom.server.extensions.Extension
import org.kodein.di.DI
import org.kodein.di.LateInitDI
import org.kodein.di.bindSingleton

val turingCoreDi: LateInitDI = LateInitDI()

fun initTuringCoreDi(extension: Extension) {
    turingCoreDi.baseDI = DI {
        @Suppress("RemoveExplicitTypeArguments")
        bindSingleton<Extension> { extension }
        importAll(
            baseModule,
            dbModule,
            configModule,
        )
    }
}