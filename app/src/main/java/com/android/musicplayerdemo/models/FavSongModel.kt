package com.example.musicplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_song_table")
data class FavSongModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "song_id") var songId: String,
    val title: String?,
    val data: String?,
    val favid: String?,
    val artist: String?,
) {
    constructor() : this(0, "", "", "", "","")
}
