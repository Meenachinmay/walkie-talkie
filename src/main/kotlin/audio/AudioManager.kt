package org.meenachinmay.audio

import javax.sound.sampled.*

class AudioManager {
    private val format = AudioFormat(44100f, 16, 1, true, true)
    private var line: TargetDataLine? = null
    private var speaker: SourceDataLine? = null
    private val bufferSize = 1024
    private var isInitialized = false

    fun initialize() {
        if (isInitialized) return

        try {
            println("🎤 Initializing audio system...")
            val info = DataLine.Info(TargetDataLine::class.java, format)

            if (!AudioSystem.isLineSupported(info)) {
                println("❌ Microphone not supported!")
                return
            }

            // This will trigger permission request
            line = AudioSystem.getLine(info) as TargetDataLine
            line?.open(format)
            println("✅ Audio system initialized successfully")
            isInitialized = true

            // Test microphone access
            testMicrophoneAccess()
        } catch (e: Exception) {
            println("❌ Error initializing audio system: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun testMicrophoneAccess() {
        try {
            line?.start()
            Thread.sleep(100) // Short test
            line?.stop()
            println("✅ Microphone access confirmed")
        } catch (e: Exception) {
            println("❌ Cannot access microphone: ${e.message}")
            e.printStackTrace()
        }
    }

//    fun startRecording(): ByteArray {
//        if (!isInitialized) {
//            println("❌ Audio system not initialized!")
//            return ByteArray(0)
//        }
//
//        try {
//            println("🎙️ Starting recording...")
//            if (line?.isActive != true) {
//                line?.start()
//            }
//
//            val buffer = ByteArray(bufferSize)
//            val bytesRead = line?.read(buffer, 0, buffer.size) ?: 0
//
//            if (bytesRead > 0) {
//                println("📢 Captured audio data: $bytesRead bytes")
//                return buffer.copyOfRange(0, bytesRead)
//            }
//
//            return ByteArray(0)
//        } catch (e: Exception) {
//            println("❌ Error recording audio: ${e.message}")
//            e.printStackTrace()
//            return ByteArray(0)
//        }
//    }

fun startRecording(): ByteArray {
    if (!isInitialized) {
        println("❌ Audio system not initialized!")
        return ByteArray(0)
    }

    try {
        if (line?.isActive != true) {
            line?.start()
            println("🎙️ Started capturing audio...")
        }

        val buffer = ByteArray(bufferSize)
        val bytesRead = line?.read(buffer, 0, buffer.size) ?: 0

        if (bytesRead > 0) {
            println("📢 Captured audio data: $bytesRead bytes")
            return buffer.copyOfRange(0, bytesRead)
        } else {
            println("⚠️ No audio data captured")
        }

        return ByteArray(0)
    } catch (e: Exception) {
        println("❌ Error recording audio: ${e.message}")
        e.printStackTrace()
        return ByteArray(0)
    }
}

    fun stopRecording() {
        try {
            if (line?.isActive == true) {
                println("⏹️ Stopping recording")
                line?.stop()
            }
        } catch (e: Exception) {
            println("❌ Error stopping recording: ${e.message}")
            e.printStackTrace()
        }
    }

    fun playAudio(audioData: ByteArray) {
        if (audioData.isEmpty()) return

        try {
            println("🔊 Playing audio of size: ${audioData.size} bytes")
            val info = DataLine.Info(SourceDataLine::class.java, format)
            speaker = AudioSystem.getLine(info) as SourceDataLine
            speaker?.open(format)
            speaker?.start()
            speaker?.write(audioData, 0, audioData.size)
            speaker?.drain()
            speaker?.stop()
            speaker?.close()
        } catch (e: Exception) {
            println("❌ Error playing audio: ${e.message}")
            e.printStackTrace()
        }
    }

    fun cleanup() {
        try {
            line?.stop()
            line?.close()
            speaker?.close()
            isInitialized = false
            println("🧹 Audio system cleaned up")
        } catch (e: Exception) {
            println("❌ Error during cleanup: ${e.message}")
        }
    }
}