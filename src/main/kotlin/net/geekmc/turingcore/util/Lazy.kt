package net.geekmc.turingcore.util

fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)