package org.meenachinmay

import org.meenachinmay.client.WalkieTalkieClient
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main() {
    println("🚀 Starting client...")
    println("📡 Connecting to localhost:50051")
    println("🎤 Press and HOLD SPACE to talk")
    println("❌ Press Ctrl+C to quit")

    val client = WalkieTalkieClient("localhost", 50051)

    Runtime.getRuntime().addShutdownHook(Thread {
        println("🛑 Shutting down client gracefully...")
        client.shutdown()
    })

    runBlocking {
        try {
            client.start()
        } catch (e: Exception) {
            println("❌ Error: ${e.message}")
            e.printStackTrace()
            exitProcess(1)
        }
    }
}