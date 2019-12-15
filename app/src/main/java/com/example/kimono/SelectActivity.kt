package com.example.kimono

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.kimono.databinding.ActivitySelectBinding

class SelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select)
        binding.image1.setOnClickListener {
            startIntent(1)
        }
        binding.image2.setOnClickListener {
            startIntent(2)
        }
        binding.image3.setOnClickListener {
            startIntent(3)
        }
        binding.image4.setOnClickListener {
            startIntent(4)
        }
        binding.image5.setOnClickListener {
            startIntent(5)
        }
        binding.image6.setOnClickListener {
            startIntent(6)
        }
        binding.image7.setOnClickListener {
            startIntent(7)
        }
        binding.image8.setOnClickListener {
            startIntent(8)
        }
    }

    private fun startIntent(id: Int) {
        MainActivity.createIntent(this, id).let {
            startActivity(it)
        }
    }
}