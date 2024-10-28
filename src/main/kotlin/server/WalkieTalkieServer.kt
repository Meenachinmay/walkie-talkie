package org.meenachinmay.server

import io.grpc.ServerBuilder

class WalkieTalkieServer(private val port: Int) {
    private val server = ServerBuilder
        .forPort(port)
        .addService(AudioStreamingService())
        .build()

    fun start() {
        server.start()
        println("Server started on port $port")
        Runtime.getRuntime().addShutdownHook(Thread {
            println("Shutting down server...")
            server.shutdown()
        })
        server.awaitTermination()
    }

    fun stop() {
        try {
            println("Stopping server...")
            server.shutdown()
            println("Server stopped successfully")
        } catch (e: Exception) {
            println("Error stopping server: ${e.message}")
        }
    }
}