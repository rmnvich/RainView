package com.example.rain

import android.content.Context

fun Context.dpToPx(dp: Float): Float {
    return resources.displayMetrics.density * dp
}