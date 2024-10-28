package org.meenachinmay

import org.meenachinmay.server.WalkieTalkieServer
import kotlin.system.exitProcess

fun main() {
    val port = 50051
    println("🚀 Starting server on port $port...")
    println("Press SPACE to talk back to clients")
    println("Press Ctrl+C to quit")

    val server = WalkieTalkieServer(port)

    // Add shutdown hook
    Runtime.getRuntime().addShutdownHook(Thread {
        println("🛑 Shutting down server gracefully...")
        server.stop()
    })

    try {
        server.start()
    } catch (e: Exception) {
        println("❌ Error running server: ${e.message}")
        exitProcess(1)
    }
}