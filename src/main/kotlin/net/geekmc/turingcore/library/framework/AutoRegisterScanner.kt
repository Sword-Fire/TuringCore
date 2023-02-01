package net.geekmc.turingcore.library.framework

import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass

internal class AutoRegisterScanner(
    private val classLoader: ClassLoader
) {
    fun scanClasses(basePackageName: String): List<KClass<*>> {
        val reflections = Reflections(
            ConfigurationBuilder
                .build(basePackageName)
                .setClassLoaders(arrayOf(classLoader))
        )
        return reflections.getTypesAnnotatedWith(AutoRegister::class.java).map {
            it.kotlin
        }
    }
}