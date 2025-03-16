package com.example.empty.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.empty.R

class ChatActivity : AppCompatActivity() {

    private lateinit var messageList: ListView
    private lateinit var recordVoiceButton: Button
    private lateinit var loadAudioButton: Button
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var adapter: ChatMessageAdapter

    // Launcher for voice recognition results.
    private val voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val voiceResult = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!voiceResult.isNullOrEmpty()) {
                val recognizedText = voiceResult[0]
                chatViewModel.sendVoiceMessage(recognizedText)
            }
        }
    }

    // Launcher for picking an audio file.
    private val audioFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { chatViewModel.sendAudioFileMessage(it, this) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageList = findViewById(R.id.messageList)
        recordVoiceButton = findViewById(R.id.recordVoiceButton)
        loadAudioButton = findViewById(R.id.loadAudioButton)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        adapter = ChatMessageAdapter(this, mutableListOf())
        messageList.adapter = adapter

        // Observe messages from the ViewModel by converting StateFlow to LiveData.
        chatViewModel.messages.asLiveData().observe(this, Observer { messages ->
            adapter.setMessages(messages)
        })

        recordVoiceButton.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            voiceLauncher.launch(intent)
        }

        loadAudioButton.setOnClickListener {
            audioFileLauncher.launch("audio/*")
        }
    }
}
