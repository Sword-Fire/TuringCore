package net.geekmc.turingcore.util.extender

import net.minestom.server.extensions.Extension
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

/**
 * 将资源拷贝至目标目录。
 * @see saveResource
 */
fun Extension.saveResource(source: String, target: String = source, replace: Boolean): Boolean {
    return this.saveResource(Path.of(source), Path.of(target), replace)
}

/**
 * 将资源拷贝至目标目录。
 *
 * @param source 在插件 jar 中 resource 文件夹下的相对资源路径。
 * @param target 在拓展文件夹下的相对目标路径。
 * @return 成功保存文件时返回 true，文件已存在且 [replace] 为 false 时返回 false，保存失败时抛出异常。
 */
fun Extension.saveResource(source: Path, target: Path = source, replace: Boolean): Boolean {
    val targetFile = dataDirectory.resolve(target)
    if (targetFile.exists() && !replace) return false
    getPackagedResource(source).use { inputStream ->
        requireNotNull(inputStream) { "Resource $source not found" }
        targetFile.createDirectories()
        Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING)
    }
    return true
}
