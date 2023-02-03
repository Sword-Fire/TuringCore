package net.geekmc.turingcore.util.extender

inline fun Boolean.whenTrue(action: () -> Unit) {
    if (this) action()
}

inline fun Boolean.whenFalse(action: () -> Unit) {
    if (!this) action()
}