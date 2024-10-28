package org.meenachinmay.server

import com.walkietalkie.grpc.AudioMessage
import com.walkietalkie.grpc.WalkieTalkieGrpcKt
import org.meenachinmay.audio.AudioManager
import org.meenachinmay.audio.KeyboardManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.google.protobuf.ByteString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope

class AudioStreamingService : WalkieTalkieGrpcKt.WalkieTalkieCoroutineImplBase() {
    private val audioManager = AudioManager()
    private val keyboardManager = KeyboardManager()
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun audioStream(requests: Flow<AudioMessage>): Flow<AudioMessage> = flow {
        println("🔌 New client connected to audio stream")

        coroutineScope {
            // Handle incoming audio
            launch {
                requests.collect { audioMessage ->
                    println("📥 Received audio chunk of size: ${audioMessage.audioData.size()} bytes")
                    audioManager.playAudio(audioMessage.audioData.toByteArray())
                }
            }

            // Handle outgoing audio
            launch {
                while (true) {
                    if (keyboardManager.isSpaceBarPressed()) {
                        val audioData = audioManager.startRecording()
                        if (audioData.isNotEmpty()) {
                            val message = AudioMessage.newBuilder()
                                .setAudioData(ByteString.copyFrom(audioData))
                                .setTimestamp(System.currentTimeMillis())
                                .build()
                            println("📤 Sending audio chunk of size: ${audioData.size} bytes")
                            emit(message)
                        }
                    }
                    kotlinx.coroutines.delay(10) // Small delay to prevent CPU overuse
                }
            }
        }
    }
}