package ru.redbyte.arch.plugin.data

import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File


fun findVirtualFile(path: String) =
    LocalFileSystem.getInstance().findFileByIoFile(
        File(path)
    )
