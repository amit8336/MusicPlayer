package com.example.musicplayer.models

import android.graphics.Bitmap

data class Audio(
    val title: String?,
    val album: String?,
    val duration: String?,
    val artist: String?,
    val data: String?,
    val thumbnail: Bitmap?,
    val id: String
)
