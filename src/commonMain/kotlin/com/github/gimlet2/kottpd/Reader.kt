package com.github.gimlet2.kottpd

/**
 * Created by Andrei Chernyshev on 1/7/22.
 */
expect class Reader {
    fun lines(): Sequence<String>
    fun read(): Int
    fun line(): String
}