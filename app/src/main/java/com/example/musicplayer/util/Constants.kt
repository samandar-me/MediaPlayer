package com.example.musicplayer.util

import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.concurrent.TimeUnit

object Constants {
    fun durationConverter(duration: Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.MICROSECONDS.toMillis(duration),
            TimeUnit.MICROSECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(duration)
                    )
        )
    }
}

fun Fragment.toast(text: String) {
    Toast.makeText(this.requireContext(), text, Toast.LENGTH_SHORT).show()
}