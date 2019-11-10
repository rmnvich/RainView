package com.example.rain

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prepareSeekBar()
    }

    private fun prepareSeekBar() {
        rainIntensitySeekBar.progress = rainView.getRainIntensity()
        rainIntensitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, from: Boolean) {
                if (progress != rainView.getRainIntensity()) {
                    rainView.setRainIntensity(progress)
                    handleWeatherTextView(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    @SuppressLint("SetTextI18n")
    private fun handleWeatherTextView(progress: Int) {
        if (progress == 0) {
            weatherTextView.text = "It's clear"
        } else weatherTextView.text = "It's raining"
    }
}
