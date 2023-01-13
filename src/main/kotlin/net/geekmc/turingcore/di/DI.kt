package net.geekmc.turingcore.di

import org.kodein.di.DI

val turingCoreDi = DI {
    importAll(
        baseModule,
        dbModule,
        configModule,
    )
}