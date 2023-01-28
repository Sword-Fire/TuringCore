package net.geekmc.turingcore.util.extender

import java.nio.file.Path

/**
 * 使 Path 可以这样写
 *
 * ```
 * val foo = Path("a") / "b" / "c"
 * ```
 * 等价于
 * ```
 * val foo = Path("a").resolve("b").resolve("c")
 * ```
 */
operator fun Path.div(path: String): Path = resolve(path)

/**
 * 使 Path 可以这样写
 *
 * ```
 * val foo = Path("a")
 * val bar = Path("b")
 * val baz = foo / bar
 * ```
 * 等价于
 * ```
 * val foo = Path("a")
 * val bar = Path("b")
 * val baz = foo.resolve(bar)
 * ```
 */
operator fun Path.div(path: Path): Path = resolve(path)
