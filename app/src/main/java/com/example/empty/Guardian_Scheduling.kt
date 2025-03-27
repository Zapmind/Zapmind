/*zaid original code

package com.example.empty

import android.app.TimePickerDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class Guardian_Scheduling : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var timeTextView: TextView
    private lateinit var messageEditText: EditText
    private lateinit var scheduleButton: Button
    private var alarmTime: Calendar? = null
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardian_scheduling)

        timeTextView = findViewById(R.id.tvTime)
        messageEditText = findViewById(R.id.etMessage)
        scheduleButton = findViewById(R.id.btnSchedule)

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        timeTextView.setOnClickListener {
            showTimePicker()
        }

        scheduleButton.setOnClickListener {
            scheduleVoiceReminder()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            isTtsInitialized = true
        } else {
            Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            alarmTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
            }
            timeTextView.text = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
        }, hour, minute, true)
        timePickerDialog.show()
    }

    private fun scheduleVoiceReminder() {
        if (alarmTime == null) {
            Toast.makeText(this, "Please select a time.", Toast.LENGTH_SHORT).show()
            return
        }
        val message = messageEditText.text.toString()
        if (message.isBlank()) {
            Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show()
            return
        }

        val currentTimeMillis = Calendar.getInstance().timeInMillis
        // If the scheduled time is before the current time, assume the user meant the next day.
        if (alarmTime!!.timeInMillis <= currentTimeMillis) {
            alarmTime!!.add(Calendar.DATE, 1)
        }
        val delayMillis = alarmTime!!.timeInMillis - currentTimeMillis

        Toast.makeText(this, "Voice reminder scheduled in ${delayMillis / 1000} seconds", Toast.LENGTH_SHORT).show()

        // Cancel any existing timer
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(delayMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Optionally update UI with remaining time.
            }

            override fun onFinish() {
                if (isTtsInitialized) {
                    tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "VOICE_REMINDER")
                } else {
                    Toast.makeText(this@Guardian_Scheduling, "TTS not ready", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}*/

//Correct code alarm rings when app is closed
package com.example.empty

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class Guardian_Scheduling : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var timeTextView: TextView
    private lateinit var messageEditText: EditText
    private lateinit var scheduleButton: Button
    private var alarmTime: Calendar? = null
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardian_scheduling)

        timeTextView = findViewById(R.id.tvTime)
        messageEditText = findViewById(R.id.etMessage)
        scheduleButton = findViewById(R.id.btnSchedule)

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        timeTextView.setOnClickListener {
            showTimePicker()
        }

        scheduleButton.setOnClickListener {
            scheduleVoiceReminder()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            isTtsInitialized = true
        } else {
            Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            alarmTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
            }
            timeTextView.text = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
        }, hour, minute, true)
        timePickerDialog.show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleVoiceReminder() {
        if (alarmTime == null) {
            Toast.makeText(this, "Please select a time.", Toast.LENGTH_SHORT).show()
            return
        }
        val message = messageEditText.text.toString()
        if (message.isBlank()) {
            Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show()
            return
        }

        val currentTimeMillis = Calendar.getInstance().timeInMillis
        if (alarmTime!!.timeInMillis <= currentTimeMillis) {
            alarmTime!!.add(Calendar.DATE, 1)  // Ensure the alarm is set for a future date
        }
        val delayMillis = alarmTime!!.timeInMillis - currentTimeMillis

        // Generate a unique ID for each alarm
        val alarmId = System.currentTimeMillis().toInt()

        // 1️⃣ Use CountDownTimer for when the app is OPEN
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(delayMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                if (isTtsInitialized) {
                    tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "VOICE_REMINDER")
                } else {
                    Toast.makeText(this@Guardian_Scheduling, "TTS not ready", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()

        // 2️⃣ Use AlarmManager for when the app is CLOSED
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("MESSAGE", message)
            putExtra("ALARM_ID", alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime!!.timeInMillis,
            pendingIntent
        )

        // Store alarm details in SharedPreferences
        val sharedPreferences = getSharedPreferences("alarms", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(alarmId.toString(), "$alarmId,${alarmTime!!.timeInMillis},$message")
        editor.apply()

        Toast.makeText(this, "Reminder set!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
