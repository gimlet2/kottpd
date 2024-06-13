package com.github.gimlet2.kottpd

import java.io.Reader
import kotlin.streams.asSequence

/**
 * Created by Andrei Chernyshev on 1/7/22.
 */
actual class Reader(reader: Reader) {
    private val bufferedReader = reader.buffered()

    actual fun lines(): Sequence<String> {
        return bufferedReader.lines().asSequence()
    }

    actual fun read(): Int {
        return bufferedReader.read()
    }

    actual fun line(): String {
        return bufferedReader.readLine()
    }
}