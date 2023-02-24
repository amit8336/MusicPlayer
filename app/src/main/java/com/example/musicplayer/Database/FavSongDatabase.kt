package com.example.musicplayer.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.musicplayer.models.FavSongModel

@Database(entities = [FavSongModel::class], version = 10)

abstract class FavSongDatabase : RoomDatabase() {

    companion object {
        var db: FavSongDatabase? = null
    }

    init {
        db = this
    }

    abstract fun favsongDao(): FavSongDao

}