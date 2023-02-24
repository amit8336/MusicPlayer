package com.example.musicplayer.Activity

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.musicplayer.ConstantClass
import com.example.musicplayer.Database.FavSongDatabase
import com.example.musicplayer.Database.FavSongDatabase.Companion.db
import com.example.musicplayer.Fragments.FavFragment
import com.example.musicplayer.Fragments.SongFragment
import com.example.musicplayer.R
import com.example.musicplayer.models.Audio
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private var bottomNav: BottomNavigationView? = null
    var songFragment = SongFragment()
    private val REQUEST_CODE = 1000

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = Room.databaseBuilder(
            applicationContext, FavSongDatabase::class.java, "fav_song_table"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
        if (checkpermisson()) {
            init()
        } else {
            askForPermission()
        }
        bottomNav = findViewById(R.id.bottomNav)
        loadFragment(songFragment)
        bottomNav!!.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.songs -> {
                    loadFragment(songFragment)
                    true
                }
                R.id.favourite -> {
                    loadFragment(FavFragment())
                    true
                }
                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
        songFragment.setInterface(object : SongFragment.SetOnClickListener {
            override fun onFavClick() {
                bottomNav!!.findViewById<BottomNavigationItemView>(R.id.favourite).performClick()
            }
        })
    }

    fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    private fun init() {
        getAllMusicFiles()
    }

    private fun getAllMusicFiles() {
        var musicList = ArrayList<Audio>()
        var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media._ID,
        )
        var cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val title = cursor.getString(0)
                val album = cursor.getString(1)
                val duration = cursor.getString(2)
                val artist = cursor.getString(3)
                val data = cursor.getString(4)
                val id = cursor.getString(5)
                val musicFile = Audio(title, album, duration, artist, data, getThumbnail(data), id)
                musicList.add(musicFile)
            }
            ConstantClass.musicList = musicList;
            ConstantClass.setBackUpList()
        } else {
            println("cursor is null")
        }
    }

    @SuppressLint("NewApi")
    private fun checkpermisson(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return checkSelfPermission(android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED


        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

        }
        return false
    }

    private fun askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_MEDIA_AUDIO,

                    ), REQUEST_CODE
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(

                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ), REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (checkpermisson()) {
                init()
            } else {
                Toast.makeText(
                    this, resources.getString(R.string.allow_permission), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        fun getThumbnail(uri: String): Bitmap? {
            val mmr = MediaMetadataRetriever()
            val rawArt: ByteArray?
            var art: Bitmap? = null
            val bfo = BitmapFactory.Options()
            try {
                mmr.setDataSource(uri)
                rawArt = mmr.embeddedPicture
                if (null != rawArt) art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.size, bfo)
                return art
            } catch (e: Exception) {
                return art
            }
        }
    }

}