package com.github.gimlet2.kottpd

expect class Writer {
    fun println(str: String = "")

    fun print(str: String = "")

    fun flush()
}
