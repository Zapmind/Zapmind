package com.example.empty.chat

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assemblyai.api.AssemblyAI
import com.assemblyai.api.resources.transcripts.types.TranscriptStatus
import com.example.empty.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

// Represents a chat message.
data class ChatMessage(val sender: Sender, val text: String)

// Enum to distinguish message sender.
enum class Sender {
    USER,
    BOT
}

class ChatViewModel : ViewModel() {
    // Holds the conversation history.
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.API_KEY
    )

    // Sends a live voice message.
    fun sendVoiceMessage(recognizedText: String) {
        _messages.value = _messages.value + ChatMessage(Sender.USER, recognizedText)
        _messages.value = _messages.value + ChatMessage(Sender.BOT, "...")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val constantPrompt = "Please explain in detail the following message and provide step-by-step instructions if tasks are mentioned: "
                val fullPrompt = constantPrompt + recognizedText
                val response = generativeModel.generateContent(
                    content { text(fullPrompt) }
                )
                val outputText = response.text ?: "No response"
                _messages.value = _messages.value.dropLast(1) + ChatMessage(Sender.BOT, outputText)
            } catch (e: Exception) {
                _messages.value = _messages.value.dropLast(1) +
                        ChatMessage(Sender.BOT, "Error: ${e.localizedMessage}")
            }
        }
    }

    // Processes a pre-recorded audio file.
    // Copies the file from the provided URI, uploads it to AssemblyAI,
    // transcribes it, and sends the transcript to Gemini.
    fun sendAudioFileMessage(audioUri: Uri, context: Context) {
        _messages.value = _messages.value + ChatMessage(Sender.USER, "Transcribing audio file...")
        _messages.value = _messages.value + ChatMessage(Sender.BOT, "...")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Copy the audio file to a temporary file.
                val tempFile = copyUriToFile(audioUri, context)
                // Build the AssemblyAI client.
                val assemblyClient = AssemblyAI.builder()
                    .apiKey("6e47846fc6fd4fa2a21f6e25f0df1f72") // Replace with your actual API key.
                    .build()
                // Upload the file.
                val fileBytes = tempFile.readBytes()
                val uploadedFile = assemblyClient.files().upload(fileBytes)
                val fileUrl = uploadedFile.uploadUrl

                // Directly call transcribe with the file URL (String).
                val transcript = assemblyClient.transcripts().transcribe(fileUrl)
                if (transcript.status == TranscriptStatus.ERROR) {
                    throw Exception("Transcript failed: ${transcript.error.orElse("Unknown error")}")
                }
                val transcribedText = transcript.getText().orElse("")
                // Remove temporary status messages.
                _messages.value = _messages.value.dropLast(2)
                // Show the transcribed text as the user's message.
                _messages.value = _messages.value + ChatMessage(Sender.USER, transcribedText)
                _messages.value = _messages.value + ChatMessage(Sender.BOT, "...")
                // Prepare the prompt for Gemini.
                val constantPrompt = "Please explain in detail the following message and provide step-by-step instructions if tasks are mentioned: "
                val fullPrompt = constantPrompt + transcribedText
                val response = generativeModel.generateContent(
                    content { text(fullPrompt) }
                )
                val outputText = response.text ?: "No response"
                _messages.value = _messages.value.dropLast(1) + ChatMessage(Sender.BOT, outputText)
            } catch (e: Exception) {
                _messages.value = _messages.value.dropLast(1) +
                        ChatMessage(Sender.BOT, "Error: ${e.localizedMessage}")
            }
        }
    }

    // Copies the content from the provided URI into a temporary File.
    private fun copyUriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Unable to open audio file")
        val tempFile = File.createTempFile("audio", ".mp3", context.cacheDir)
        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}
