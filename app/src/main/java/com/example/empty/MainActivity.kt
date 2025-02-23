package com.example.empty

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Make sure your layout file is named activity_main.xml

        // Find the button by its ID and set a click listener
//        val nextButton = findViewById<Button>(R.id.b1)
//        nextButton.setOnClickListener {
//            // Create an intent to start SecondActivity
//            val intent = Intent(this, SecondActivity::class.java)
//            startActivity(intent)
//        }
        //1
        val games = findViewById<Button>(R.id.btn_Games)
        games.setOnClickListener {
            // Create an intent to start SecondActivity
            val intent = Intent(this, Games::class.java)
            startActivity(intent)
        }
        //2
        val progress1 = findViewById<Button>(R.id.btn_Progress)
        progress1.setOnClickListener {
            // Create an intent to start SecondActivity
            val intent = Intent(this, Progress::class.java)
            startActivity(intent)
        }
        //3
        val guardianscheduling1 = findViewById<Button>(R.id.btn_Guardian_scheduling)
        guardianscheduling1.setOnClickListener {
            // Create an intent to start SecondActivity
            val intent = Intent(this, Guardian_Scheduling::class.java)
            startActivity(intent)
        }
        //4
        val whatitis1 = findViewById<Button>(R.id.btn_What_It_Is)
        whatitis1.setOnClickListener {
            // Create an intent to start SecondActivity
            val intent = Intent(this, What_it_is::class.java)
            startActivity(intent)
        }
        //5
        val whattheysaid1 = findViewById<Button>(R.id.btn_What_They_Said)
        whattheysaid1.setOnClickListener {
            // Create an intent to start SecondActivity
            val intent = Intent(this, What_they_said::class.java)
            startActivity(intent)
        }
        //6
        val flashcards1 = findViewById<Button>(R.id.btn_Flashcards)
        flashcards1.setOnClickListener {
            // Create an intent to start SecondActivity
            val intent = Intent(this, Flashcards::class.java)
            startActivity(intent)
        }
    }
}
