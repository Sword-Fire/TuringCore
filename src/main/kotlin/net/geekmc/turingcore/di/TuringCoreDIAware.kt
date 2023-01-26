package net.geekmc.turingcore.di

import org.kodein.di.DI
import org.kodein.di.DIAware

interface TuringCoreDIAware : DIAware {
    override val di: DI get() = turingCoreDi
}