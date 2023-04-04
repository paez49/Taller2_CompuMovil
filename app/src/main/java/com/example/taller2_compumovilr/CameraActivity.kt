package com.example.taller2_compumovilr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taller2_compumovilr.databinding.ActivityCameraBinding
import com.example.taller2_compumovilr.databinding.ActivityMainBinding

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
}