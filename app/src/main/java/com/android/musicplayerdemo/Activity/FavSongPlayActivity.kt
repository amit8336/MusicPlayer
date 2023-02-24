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
import com.example.musicplayer.Database.FavSongDatabase.Companion.db
import com.example.musicplayer.R
import com.example.musicplayer.interfaces.PlayerInterface
import com.example.musicplayer.models.FavSongModel
import java.util.*

class FavSongPlayActivity : AppCompatActivity(), PlayerInterface {

    private var fav_next_song_btn: ImageView? = null
    private var fav_rewind_song_btn: ImageView? = null
    private var back_button: ImageView? = null
    private var fav_play_btn: ImageView? = null
    private var fav_current_duration: TextView? = null
    private var artist_name: TextView? = null
    private var song_title: TextView? = null
    private var fav_total_duration: TextView? = null
    private var fav_seekbar: SeekBar? = null
    var pos: Int? = null
    var mediaPlayer: MediaPlayer? = null
    var favlist: List<FavSongModel>? = null
    var isPlaying = false
    var timer: Timer = Timer()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_song_play)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        fav_next_song_btn = findViewById(R.id.fav_next_song_btn)
        fav_rewind_song_btn = findViewById(R.id.fav_rewind_song_btn)
        fav_play_btn = findViewById(R.id.fav_play_btn)
        fav_total_duration = findViewById(R.id.fav_total_duration)
        fav_current_duration = findViewById(R.id.fav_current_duration)
        artist_name = findViewById(R.id.artist_name)
        back_button = findViewById(R.id.back_button)
        song_title = findViewById(R.id.song_title)
        fav_seekbar = findViewById(R.id.fav_seekbar)
        favlist = db!!.favsongDao().getAll()
        pos = intent.getIntExtra("pos", 0)

        click()
        playMusic()
        setTitleAndArtistName()
    }

    fun click() {
        fav_play_btn!!.setOnClickListener(View.OnClickListener {
            musicPlayPause()
        })
        fav_next_song_btn!!.setOnClickListener(View.OnClickListener {
            nextSong()
        })
        fav_rewind_song_btn!!.setOnClickListener(View.OnClickListener {
            previousSong()
        })
        back_button!!.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
    }

    private fun initialize() {
        playMusic()
    }

    private fun playMusic() {
        isPlaying = true
        fav_play_btn!!.setImageDrawable(resources.getDrawable(R.drawable.pausebtn))
        val path = Uri.parse(favlist!!.get(pos!!).data)
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
        setSeekBar()
    }

    private fun setSeekBar() {
        fav_seekbar!!.progressDrawable.setColorFilter(
            Color.parseColor("#F94D95"), PorterDuff.Mode.MULTIPLY
        )
        fav_seekbar!!.thumb.setColorFilter(Color.parseColor("#F94D95"), PorterDuff.Mode.SRC_ATOP)
        fav_seekbar!!.max = mediaPlayer?.duration!!
        fav_total_duration!!.text = convertSecondsToSsMm(mediaPlayer?.duration!!)
        fav_seekbar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer?.seekTo(progress)
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
        fav_seekbar!!.setProgress(mediaPlayer?.currentPosition!!)
        runOnUiThread(Runnable {
            fav_current_duration!!.text = convertSecondsToSsMm(mediaPlayer?.currentPosition!!)
        })
    }

    fun convertSecondsToSsMm(seconds: Int): String? {
        val s = (seconds / 1000) % 60
        val m = (seconds / 1000) / 60
        return String.format("%02d:%02d", m, s)
    }

    override fun musicPlayPause() {
        isPlaying = !isPlaying
        if (isPlaying) resumeMusic()
        else pauseMusic()
    }

    override fun nextSong() {
        var position = pos
        position = position!! + 1
        if (favlist!!.size > position) {
            pos = pos!! + 1
            initialize()
        } else {
            Toast.makeText(this, resources.getString(R.string.last_song), Toast.LENGTH_SHORT).show()
        }
    }

    override fun previousSong() {
        var position = pos
        position = position!! - 1
        if (0 <= position) {
            pos = pos!! - 1
            initialize()
        } else {
            Toast.makeText(this, resources.getString(R.string.first_song), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun resumeMusic() {
        isPlaying = true
        fav_play_btn!!.setImageDrawable(resources.getDrawable(R.drawable.pausebtn))
        if (mediaPlayer != null) {
            mediaPlayer?.start()
        }
    }

    private fun pauseMusic() {
        isPlaying = false
        fav_play_btn!!.setImageDrawable(resources.getDrawable(R.drawable.playbtn))
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

    private fun setTitleAndArtistName() {
        artist_name!!.text = favlist!!.get(pos!!).artist
        song_title!!.text = favlist!!.get(pos!!).title
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