package com.github.gimlet2.kottpd

import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Created by Andrei Chernyshev on 1/14/22.
 */
actual class ServerSocket actual constructor(port: Int, secure: Boolean, keyStoreFile: String, password: String) {

    private val serverSocket: ServerSocket by lazy {
        if (secure) {
            secureSocket(port, keyStoreFile, password)
        } else {
            ServerSocket(port)
        }
    }

    actual fun accept(): Socket {
        return serverSocket.accept()
            .let {
                Socket(
                    Reader(InputStreamReader(it.getInputStream())),
                    Writer(OutputStreamWriter(it.getOutputStream())),
                    it::close
                )
            }
    }

    actual fun close() {
        serverSocket.close()
    }

    private fun secureSocket(port: Int, keyStoreFile: String, password: String): ServerSocket {

        /* Create keystore */
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(FileInputStream(keyStoreFile), password.toCharArray())

        /* Get factory for the given keystore */
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply { init(keyStore) }
        val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            .apply { init(keyStore, password.toCharArray()) }
        return SSLContext.getInstance("SSL").apply { init(kmf.keyManagers, null, null) }
            .serverSocketFactory.createServerSocket(port)

    }
}