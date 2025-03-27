package com.example.empty

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.*

class ReminderReceiver : BroadcastReceiver(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var message: String? = null
    private var isTtsInitialized = false

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        message = intent.getStringExtra("MESSAGE")

        if (message != null) {
            Toast.makeText(context, "Reminder: $message", Toast.LENGTH_LONG).show()
        }

        // Initialize Text-to-Speech
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            isTtsInitialized = true
            tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "VOICE_REMINDER")
        }
    }
}