package com.example.rain

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prepareIntensitySeekBar()
        prepareAngleSeekBar()
    }

    private fun prepareIntensitySeekBar() {
        rainIntensitySeekBar.progress = rainView.getRainIntensity()
        rainIntensitySeekBar.setOnProgressChangeListener { progress ->
            if (progress != rainView.getRainIntensity()) {
                rainView.setRainIntensity(progress)
                handleWeatherTextView(progress)
            }
        }
    }

    private fun prepareAngleSeekBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            rainAngleSeekBar.min = -25
        }
        rainAngleSeekBar.progress = rainView.getRainAngle()
        rainAngleSeekBar.setOnProgressChangeListener { progress ->
            if (progress != rainView.getRainAngle()) {
                rainView.setRainAngle(progress)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleWeatherTextView(progress: Int) {
        if (progress == 0) {
            weatherTextView.text = "It's clear"
        } else weatherTextView.text = "It's raining"
    }
}
