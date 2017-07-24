//package org.kottpd.pal.native
//import kotlinx.cinterop.*
//import sockets.*
//
//val errno: Int
//    get() = interop_errno()
//
//fun htons(value: Short) = interop_htons(value.toInt()).toShort()
//
//inline fun Int.ensureUnixCallResult(predicate: (Int) -> Boolean): Int {
//    if (!predicate(this)) {
//        throw Error(strerror(errno)!!.toKString())
//    }
//    return this
//}
//
//inline fun Long.ensureUnixCallResult(predicate: (Long) -> Boolean): Long {
//    if (!predicate(this)) {
//        throw Error(strerror(errno)!!.toKString())
//    }
//    return this
//}