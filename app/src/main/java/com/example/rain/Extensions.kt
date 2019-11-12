package com.example.rain

import android.content.Context
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

fun Context.dpToPx(dp: Float): Float {
    return resources.displayMetrics.density * dp
}

fun Float.degToRad(): Float {
    return this * Math.PI.toFloat() / 180
}

inline fun AppCompatSeekBar.setOnProgressChangeListener(crossinline onProgressChanged: (Int) -> Unit) {
    return this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) =
            onProgressChanged(progress)
    })
}