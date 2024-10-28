package org.meenachinmay.client

import com.walkietalkie.grpc.AudioMessage
import com.walkietalkie.grpc.WalkieTalkieGrpcKt
import org.meenachinmay.audio.AudioManager
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.flow
import com.google.protobuf.ByteString
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalkieTalkieClient(private val host: String, private val port: Int) {
    private val channel = ManagedChannelBuilder
        .forAddress(host, port)
        .usePlaintext()
        .build()

    private val stub = WalkieTalkieGrpcKt.WalkieTalkieCoroutineStub(channel)
    private val audioManager = AudioManager()
    private var isRecording = false

    suspend fun start() {
        println("🎤 Initializing audio system...")
        audioManager.initialize()
        println("🎤 Starting audio streaming...")

        val audioFlow = flow {
            // Wait for 2 seconds before starting
            println("⏳ Waiting 2 seconds before recording...")
            delay(2000)

            println("🎙️ Starting 5-second recording...")
            isRecording = true

            // Record for 5 seconds
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 5000) {
                val audioData = audioManager.startRecording()
                if (audioData.isNotEmpty()) {
                    val message = AudioMessage.newBuilder()
                        .setAudioData(ByteString.copyFrom(audioData))
                        .setTimestamp(System.currentTimeMillis())
                        .build()
                    println("📤 Sending audio chunk: ${audioData.size} bytes")
                    emit(message)
                }
                delay(10) // Small delay between chunks
            }

            println("⏹️ Recording completed")
            isRecording = false
            audioManager.stopRecording()
        }

        coroutineScope {
            launch {
                try {
                    stub.audioStream(audioFlow).collect { audioMessage ->
                        println("📥 Received audio chunk: ${audioMessage.audioData.size()} bytes")
                        audioManager.playAudio(audioMessage.audioData.toByteArray())
                    }
                } catch (e: Exception) {
                    println("❌ Error in audio streaming: ${e.message}")
                    e.printStackTrace()
                    throw e
                }
            }
        }
    }

    fun shutdown() {
        println("👋 Shutting down client...")
        audioManager.cleanup()
        channel.shutdown()
    }
}