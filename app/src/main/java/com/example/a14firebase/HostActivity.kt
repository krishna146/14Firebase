package com.example.a14firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.a14firebase.R
import com.example.a14firebase.databinding.ActivityHostBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}