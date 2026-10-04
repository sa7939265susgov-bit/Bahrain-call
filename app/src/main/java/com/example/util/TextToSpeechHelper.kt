package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechHelper(context: Context, onInitSuccess: () -> Unit = {}) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("TTS", "Failed to initialize TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isReady = true
            tts?.language = Locale.US
        } else {
            Log.e("TTS", "TextToSpeech initialization failed.")
        }
    }

    fun speak(text: String, isArabic: Boolean = false) {
        if (!isReady || tts == null) return

        if (isArabic) {
            val result = tts?.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to English if Arabic voice pack not installed on system
                tts?.language = Locale.US
            }
        } else {
            tts?.language = Locale.US
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "BAHRAIN_MEET_TTS")
    }

    fun stop() {
        if (isReady) {
            tts?.stop()
        }
    }

    fun shutdown() {
        if (isReady) {
            tts?.stop()
            tts?.shutdown()
        }
    }
}
