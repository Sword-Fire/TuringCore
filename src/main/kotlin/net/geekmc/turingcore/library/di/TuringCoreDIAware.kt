package net.geekmc.turingcore.library.di

import org.kodein.di.DI
import org.kodein.di.DIAware

interface TuringCoreDIAware : DIAware {
    override val di: DI get() = turingCoreDi
}