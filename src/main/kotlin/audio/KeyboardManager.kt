package org.meenachinmay.audio

import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent

class KeyboardManager {
    private var isSpacePressed = false

    init {
        println("⌨️ Initializing keyboard manager...")
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher { e ->
            when (e.id) {
                KeyEvent.KEY_PRESSED -> {
                    if (e.keyCode == KeyEvent.VK_SPACE && !isSpacePressed) {
                        isSpacePressed = true
                        println("🎙️ Space pressed - Starting to talk")
                    }
                }
                KeyEvent.KEY_RELEASED -> {
                    if (e.keyCode == KeyEvent.VK_SPACE) {
                        isSpacePressed = false
                        println("🎙️ Space released - Stopped talking")
                    }
                }
            }
            false
        }
    }

    fun isSpaceBarPressed() = isSpacePressed
}