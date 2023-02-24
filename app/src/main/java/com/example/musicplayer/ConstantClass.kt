package com.example.musicplayer

import com.example.musicplayer.models.Audio

class ConstantClass {

    companion object {
        var backUpList = ArrayList<Audio>()
        var musicList = ArrayList<Audio>()
        var currentListIndex = 0

        fun setBackUpList() {
            backUpList = musicList
        }

        fun setMusicListForHome() {
            musicList = backUpList
        }
    }
}