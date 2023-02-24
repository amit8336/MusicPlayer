package com.example.musicplayer.Activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.ConstantClass
import com.example.musicplayer.Database.FavSongDatabase.Companion.db
import com.example.musicplayer.R
import com.example.musicplayer.models.FavSongModel
import java.util.*

class PlayActivity : AppCompatActivity(), com.example.musicplayer.interfaces.PlayerInterface {
    private var rewind_song_btn: ImageView? = null
    private var current_duration: TextView? = null
    private var total_duration: TextView? = null
    private var artist_name: TextView? = null
    private var song_title: TextView? = null
    private var seekbar: SeekBar? = null
    private var play_btn: ImageView? = null
    private var favourite: ImageView? = null
    private var back_btn: ImageView? = null
    private var next_song_btn: ImageView? = null

    var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
    var isFavourite = false
    var favouriteSongId = -1
    var timer: Timer = Timer()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        //remove statusbar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        seekbar = findViewById(R.id.seekbar)
        favourite = findViewById(R.id.favourite)
        total_duration = findViewById(R.id.total_duration)
        current_duration = findViewById(R.id.current_duration)
        next_song_btn = findViewById(R.id.next_song_btn)
        rewind_song_btn = findViewById(R.id.rewind_song_btn)
        artist_name = findViewById(R.id.artist_name)
        song_title = findViewById(R.id.song_title)
        back_btn = findViewById(R.id.back_btn)
        play_btn = findViewById(R.id.play_btn)
        playMusic()
        clicks()

    }

    private fun clicks() {
        rewind_song_btn!!.setOnClickListener {
            previousSong()
        }
        next_song_btn!!.setOnClickListener {
            nextSong()
        }
        play_btn!!.setOnClickListener {
            musicPlayPause()
            setSeekBar()
        }
        favourite!!.setOnClickListener {
            favUnfavSong()
        }
        back_btn!!.setOnClickListener{
            onBackPressed()
        }
    }


    private fun initialize() {
        playMusic()
    }

    private fun playMusic() {
        isPlaying = true
        play_btn!!.setImageDrawable(resources.getDrawable(R.drawable.pausebtn))
        val path = Uri.parse(ConstantClass.musicList.get(ConstantClass.currentListIndex).data)
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            mediaPlayer = MediaPlayer.create(this, path)
            mediaPlayer?.start()
        } else {
            mediaPlayer = MediaPlayer.create(this, path)
            mediaPlayer?.start()
        }
        mediaPlayer?.setOnCompletionListener {
            nextSong()
        }
        setTitleAndArtistName()
        setSeekBar()
        checkIfFavourite()
    }

    private fun setSeekBar() {
        seekbar!!.progressDrawable.setColorFilter(
            Color.parseColor("#F94D95"), PorterDuff.Mode.MULTIPLY
        )
        seekbar!!.thumb.setColorFilter(Color.parseColor("#F94D95"), PorterDuff.Mode.SRC_ATOP)
        seekbar!!.max = mediaPlayer?.duration!!
        total_duration!!.text = convertSecondsToSsMm(mediaPlayer?.duration!!)


        seekbar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer?.seekTo(progress)
                    seekBar!!.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

                updateSeekBar()
            }
        }, 0, 1000)
    }

    fun updateSeekBar() {
        try {
            seekbar!!.progress = mediaPlayer?.currentPosition!!
            runOnUiThread(Runnable {
                current_duration!!.text = convertSecondsToSsMm(mediaPlayer?.currentPosition!!)
            })
        } catch (e: Exception) {
        }
    }

    fun convertSecondsToSsMm(seconds: Int): String? {
        val s = (seconds / 1000) % 60
        val m = (seconds / 1000) / 60
        return String.format("%02d:%02d", m, s)
    }

    override fun nextSong() {
        var position = ConstantClass.currentListIndex
        position++
        if (ConstantClass.musicList.size > position) {
            ConstantClass.currentListIndex++
            initialize()
        } else {
            Toast.makeText(this, resources.getString(R.string.last_song), Toast.LENGTH_SHORT).show()
        }
    }

    override fun previousSong() {
        var position = ConstantClass.currentListIndex
        position--
        if (0 <= position) {
            ConstantClass.currentListIndex--
            initialize()
        } else {
            Toast.makeText(this, resources.getString(R.string.first_song), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun musicPlayPause() {
        isPlaying = !isPlaying
        if (isPlaying) resumeMusic()
        else pauseMusic()
    }

    private fun resumeMusic() {
        isPlaying = true
        play_btn!!.setImageDrawable(resources.getDrawable(R.drawable.pausebtn))
        if (mediaPlayer != null) {
            mediaPlayer?.start()
        }
    }

    private fun pauseMusic() {
        isPlaying = false
        play_btn!!.setImageDrawable(resources.getDrawable(R.drawable.playbtn))
        if (mediaPlayer != null) {
            mediaPlayer?.pause()
        }
    }

    private fun stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
    }

    private fun favUnfavSong() {
        val songId = ConstantClass.musicList.get(ConstantClass.currentListIndex).id
        val songName = ConstantClass.musicList.get(ConstantClass.currentListIndex).title
        val songData = ConstantClass.musicList.get(ConstantClass.currentListIndex).data
        val database = db!!.favsongDao().getAll()
        val list = database
        isFavourite = checkForCurrentSong(list)
        if (mediaPlayer != null) {
            if (isFavourite) {
                db!!.favsongDao().delete(
                    FavSongModel(
                        songId = songId,
                        id = favouriteSongId,
                        title = songName,
                        data = songData,
                        favid = "",
                        artist = " "
                    )
                )
                favourite!!.setImageResource(R.drawable.unfavourite)
                Toast.makeText(this, "removed from favourite", Toast.LENGTH_SHORT).show()
            } else {
                db!!.favsongDao().insert(
                    FavSongModel(
                        songId = songId, id = 0, title = songName, data = songData, favid = "", artist = ""
                    )
                )
                favourite!!.setImageResource(R.drawable.favourite)
                Toast.makeText(this, "added to favourite", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfFavourite() {
        val database = db!!.favsongDao().getAll()
        val list = database
        isFavourite = checkForCurrentSong(list)
        if (isFavourite) {
            favourite!!.setImageResource(R.drawable.favourite)
            isFavourite = true
        } else {
            favourite!!.setImageResource(R.drawable.unfavourite)
            isFavourite = false
        }
    }

    private fun checkForCurrentSong(list: List<FavSongModel>): Boolean {
        var isFound = false
        val currentId = ConstantClass.musicList.get(ConstantClass.currentListIndex).id
        list.forEach {
            if (it.songId == currentId) {
                isFound = true
                favouriteSongId = it.id
            }
        }
        return isFound
    }
    private fun setTitleAndArtistName() {
        artist_name!!.text = ConstantClass.musicList.get(ConstantClass.currentListIndex).artist
        song_title!!.text = ConstantClass.musicList.get(ConstantClass.currentListIndex).title

        artist_name!!.isSelected = true
        song_title!!.isSelected = true
    }

    override fun onBackPressed() {
        timer.cancel()
        stopMusic()
        super.onBackPressed()
        finish()
    }
}