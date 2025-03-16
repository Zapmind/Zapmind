package com.example.empty.games

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.empty.R
import com.example.empty.databinding.ActivityWordFinderBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class WordFinderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordFinderBinding
    private var startTime: Long = 0L
    private val baseScore = 1000          // Maximum score if answered instantly
    private val penaltyPerSecond = 10     // Points deducted per second elapsed

    private lateinit var currentLetters: String
    private lateinit var validWords: List<String>
    private var minCount: Int = 0
    private val foundWords = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordFinderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadRandomLetterSet()
        startNewGame()

        binding.btnSubmit.setOnClickListener {
            checkWord()
        }

        binding.btnNewGame.setOnClickListener {
            loadRandomLetterSet()
            startNewGame()
        }
    }

    private fun loadRandomLetterSet() {
        try {
            val inputStream = resources.openRawResource(R.raw.letters)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            // Choose a random set
            val randomIndex = (0 until jsonArray.length()).random()
            val jsonObject: JSONObject = jsonArray.getJSONObject(randomIndex)

            currentLetters = jsonObject.getString("letters")
            validWords = (0 until jsonObject.getJSONArray("validWords").length()).map {
                jsonObject.getJSONArray("validWords").getString(it).uppercase()
            }
            minCount = jsonObject.getInt("minCount")

            binding.tvLetters.text = currentLetters
            binding.tvMinWords.text = "Enter at least $minCount words"
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load letters", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startNewGame() {
        foundWords.clear()
        binding.etWord.text.clear()
        binding.tvScore.text = "Score: "
        binding.tvFoundWords.text = ""
        binding.chronometer.base = SystemClock.elapsedRealtime()
        startTime = SystemClock.elapsedRealtime()
        binding.chronometer.start()
    }

    private fun checkWord() {
        val userWord = binding.etWord.text.toString().uppercase().trim()

        if (userWord.isEmpty()) {
            Toast.makeText(this, "Please enter a word.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!validWords.contains(userWord)) {
            Toast.makeText(this, "Wrong word! Try again.", Toast.LENGTH_SHORT).show()
        } else if (foundWords.contains(userWord)) {
            Toast.makeText(this, "Word already entered.", Toast.LENGTH_SHORT).show()
        } else {
            foundWords.add(userWord)
            updateFoundWordsDisplay()
            binding.etWord.text.clear()

            if (foundWords.size >= minCount) {
                finishGame()
            } else {
                Toast.makeText(
                    this,
                    "Good! ${minCount - foundWords.size} more word(s) to go.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateFoundWordsDisplay() {
        binding.tvFoundWords.text = "Found Words:\n" + foundWords.joinToString("\n")
    }

    private fun finishGame() {
        binding.chronometer.stop()
        val elapsedMillis = SystemClock.elapsedRealtime() - binding.chronometer.base
        val elapsedSeconds = elapsedMillis / 1000.0
        val score = (baseScore - (penaltyPerSecond * elapsedSeconds)).toInt().coerceAtLeast(0)
        binding.tvScore.text = "Score: $score (Time: ${"%.1f".format(elapsedSeconds)} s)"
        Toast.makeText(this, "Challenge complete! You found $minCount words.", Toast.LENGTH_LONG).show()
    }
}
