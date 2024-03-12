package com.github.gimlet2.kottpd

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import platform.posix.fsync
import platform.posix.write

actual class Writer(private val source: Int) {
    actual fun println(str: String) {
        if (str.isEmpty()) {
            print("\n")
        } else {
            print(str + "\n")
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun print(str: String) {
        memScoped {
            write(source, str.cstr.getPointer(this), str.length.toULong())
        }
    }

    actual fun flush() {
        fsync(source)
    }

}