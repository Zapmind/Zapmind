package com.example.empty

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.empty.databinding.ActivityGamesBinding

class Games : AppCompatActivity() {

    private lateinit var binding: ActivityGamesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnWordFinder.setOnClickListener {
            // Launch WordFinderActivity (to be implemented)
            val intent = Intent(this, com.example.empty.games.WordFinderActivity::class.java)
            startActivity(intent)
        }

        binding.btnGame2.setOnClickListener {
            // For now, show a placeholder
            // You can implement game 2 later
        }

        binding.btnGame3.setOnClickListener {
            // For now, show a placeholder
            // You can implement game 3 later
        }
    }
}
