package ru.redbyte.arch.plugin.data.utils

import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File

fun findVirtualFile(path: String) =
    LocalFileSystem.getInstance().findFileByIoFile(
        File(path)
    )

fun List<String>.sortedImports(): List<String> {
    return sorted().sortedBy { it.contains("javax") }
}