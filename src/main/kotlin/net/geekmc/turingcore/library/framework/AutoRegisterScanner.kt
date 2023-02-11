package net.geekmc.turingcore.library.framework

import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import kotlin.reflect.KClass

internal class AutoRegisterScanner(
    private val classLoader: ClassLoader
) {
    fun scanClasses(basePackageName: String): List<KClass<*>> {
        val reflections = Reflections(
            ConfigurationBuilder().apply {
                addClassLoaders(classLoader)
                forPackage(basePackageName, classLoader)
                filterInputsBy(FilterBuilder().apply {
                    includePackage(basePackageName)
                })
            }
        )
        return reflections.getTypesAnnotatedWith(AutoRegister::class.java).map {
            it.kotlin
        }
    }
}