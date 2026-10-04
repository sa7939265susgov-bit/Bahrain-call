package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.Collections

/**
 * Helper to synthesize authentic Bahrain telecom telephone tones.
 * Standard Bahrain Ringback Tone (Batelco / TRA Bahrain):
 * Dual frequencies: 400.0 Hz + 450.0 Hz
 * Cadence: 400ms Tone ON, 200ms OFF, 400ms Tone ON, 2000ms OFF (repeats twice before connecting).
 */
class ToneRingtoneHelper {

    private var toneGenerator: ToneGenerator? = null
    private val handler = Handler(Looper.getMainLooper())
    private val activeAudioTracks = Collections.synchronizedList(mutableListOf<AudioTrack>())

    @Volatile
    var isPlayingRingtone: Boolean = false
        private set

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (e: Exception) {
            Log.e("ToneRingtoneHelper", "Failed to initialize ToneGenerator: ${e.message}")
        }
    }

    /**
     * Synthesizes PCM dual-tone waveform for the Bahraini ringback tone (400 Hz + 450 Hz)
     * with smooth fade envelope to avoid clicks and pops.
     */
    private fun playPcmDualToneBeep(durationMs: Int = 400, freq1: Double = 400.0, freq2: Double = 450.0) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)
            val fadeLength = (sampleRate * 0.025).toInt() // 25ms smooth fade envelope to avoid clicks

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val wave1 = Math.sin(2.0 * Math.PI * freq1 * t)
                val wave2 = Math.sin(2.0 * Math.PI * freq2 * t)

                var envelope = 0.85
                if (i < fadeLength) {
                    envelope *= (i.toDouble() / fadeLength)
                } else if (i > numSamples - fadeLength) {
                    envelope *= ((numSamples - i).toDouble() / fadeLength)
                }

                val combined = ((wave1 + wave2) / 2.0 * Short.MAX_VALUE * envelope).toInt()
                samples[i] = combined.toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            activeAudioTracks.add(audioTrack)
            audioTrack.write(samples, 0, numSamples)
            audioTrack.play()

            handler.postDelayed({
                try {
                    activeAudioTracks.remove(audioTrack)
                    audioTrack.stop()
                    audioTrack.release()
                } catch (e: Exception) {
                    Log.e("ToneRingtoneHelper", "AudioTrack cleanup error: ${e.message}")
                }
            }, durationMs.toLong() + 50L)
        } catch (e: Exception) {
            Log.e("ToneRingtoneHelper", "AudioTrack fail, fallback to ToneGenerator: ${e.message}")
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_SUP_RINGTONE, durationMs)
            } catch (t: Exception) {
                Log.e("ToneRingtoneHelper", "ToneGenerator error: ${t.message}")
            }
        }
    }

    /**
     * Plays the authentic Bahrain telephone ringback tone:
     * Dual tones: 400 Hz + 450 Hz
     * Cadence:
     * - Beep 1: 400ms
     * - Silence: 200ms
     * - Beep 2: 400ms
     * - Pause: 2000ms (2 seconds silence)
     * Exactly repeats `repeats` times (default 2) before triggering `onConnected`.
     */
    fun playBahrainiOutgoingRingtone(
        repeats: Int = 2,
        onRingChange: (currentRing: Int, totalRings: Int) -> Unit = { _, _ -> },
        onConnected: () -> Unit
    ) {
        stop()
        isPlayingRingtone = true

        val beepDuration = 400 // 400ms pulse
        val beepGap = 200 // 200ms gap between double-beeps
        val pauseDuration = 2000 // 2000ms inter-ring silence
        val cycleDuration = beepDuration + beepGap + beepDuration + pauseDuration // 3000ms per repetition

        fun playRingCycle(currentRepeat: Int) {
            if (!isPlayingRingtone) return

            onRingChange(currentRepeat, repeats)

            // Pulse 1
            playPcmDualToneBeep(durationMs = beepDuration, freq1 = 400.0, freq2 = 450.0)

            // Pulse 2 after 600ms (400ms pulse + 200ms gap)
            handler.postDelayed({
                if (!isPlayingRingtone) return@postDelayed
                playPcmDualToneBeep(durationMs = beepDuration, freq1 = 400.0, freq2 = 450.0)
            }, (beepDuration + beepGap).toLong())

            if (currentRepeat < repeats) {
                // Schedule next repetition after full cycle (3000ms)
                handler.postDelayed({
                    if (!isPlayingRingtone) return@postDelayed
                    playRingCycle(currentRepeat + 1)
                }, cycleDuration.toLong())
            } else {
                // Last repetition finished: after pulse 2 ends (at 1000ms) + 600ms connect handover pause = 1600ms
                handler.postDelayed({
                    if (!isPlayingRingtone) return@postDelayed
                    isPlayingRingtone = false
                    onConnected()
                }, (beepDuration + beepGap + beepDuration + 600L))
            }
        }

        playRingCycle(1)
    }

    /**
     * Plays authentic Bahrain ringtone sound (repeating twice before connecting)
     */
    fun playRingtoneSound(durationMs: Long = 4800L, onComplete: () -> Unit = {}) {
        playBahrainiOutgoingRingtone(repeats = 2, onConnected = onComplete)
    }

    /**
     * Plays a short chirp beep for IVR keypad selection feedback
     */
    fun playShortBeep(durationMs: Long = 300L, onComplete: () -> Unit = {}) {
        try {
            playPcmDualToneBeep(durationMs = durationMs.toInt(), freq1 = 400.0, freq2 = 450.0)
            handler.postDelayed({
                onComplete()
            }, durationMs + 50L)
        } catch (e: Exception) {
            onComplete()
        }
    }

    /**
     * Plays DTMF telephone key tone sound (1, 2, 3, etc.)
     */
    fun playDtmfTone(digit: String, durationMs: Long = 250L) {
        val tone = when (digit) {
            "1" -> ToneGenerator.TONE_DTMF_1
            "2" -> ToneGenerator.TONE_DTMF_2
            "3" -> ToneGenerator.TONE_DTMF_3
            "4" -> ToneGenerator.TONE_DTMF_4
            "5" -> ToneGenerator.TONE_DTMF_5
            "6" -> ToneGenerator.TONE_DTMF_6
            "7" -> ToneGenerator.TONE_DTMF_7
            "8" -> ToneGenerator.TONE_DTMF_8
            "9" -> ToneGenerator.TONE_DTMF_9
            "0" -> ToneGenerator.TONE_DTMF_0
            "*" -> ToneGenerator.TONE_DTMF_S
            "#" -> ToneGenerator.TONE_DTMF_P
            else -> ToneGenerator.TONE_PROP_BEEP
        }
        try {
            toneGenerator?.startTone(tone, durationMs.toInt())
        } catch (e: Exception) {
            Log.e("ToneRingtoneHelper", "Error playing DTMF tone: ${e.message}")
        }
    }

    fun stop() {
        isPlayingRingtone = false
        handler.removeCallbacksAndMessages(null)
        synchronized(activeAudioTracks) {
            for (track in activeAudioTracks) {
                try {
                    track.stop()
                    track.release()
                } catch (e: Exception) {
                    // Ignore
                }
            }
            activeAudioTracks.clear()
        }
        try {
            toneGenerator?.stopTone()
        } catch (e: Exception) {
            Log.e("ToneRingtoneHelper", "Error stopping tone: ${e.message}")
        }
    }

    fun release() {
        stop()
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.e("ToneRingtoneHelper", "Error releasing tone generator: ${e.message}")
        }
    }
}
