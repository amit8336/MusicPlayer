package com.example.musicplayer.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.Activity.PlayActivity
import com.example.musicplayer.Adapters.MusicAdapter
import com.example.musicplayer.ConstantClass
import com.example.musicplayer.R

class SongFragment : Fragment() {
    private var rv_view: RecyclerView? = null
    private var song_not_available: ImageView? = null
    private var setOnClickListener: SetOnClickListener? = null
    private var progress_bar: ProgressBar? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song, container, false)

        return view
    }

    override fun onResume() {
        super.onResume()
        rv_view = view?.findViewById(R.id.rv_view)
        song_not_available = view?.findViewById(R.id.not_available)
        progress_bar = view?.findViewById(R.id.progress_bar)
        ConstantClass.setMusicListForHome()


        if (ConstantClass.musicList != null && ConstantClass.musicList.size != 0) {
            progress_bar!!.setVisibility(View.GONE)
            song_not_available!!.setVisibility(View.GONE)
            rv_view!!.setVisibility(View.VISIBLE)
            initList()
        } else {
            progress_bar!!.setVisibility(View.VISIBLE)
            song_not_available!!.setVisibility(View.VISIBLE)
            rv_view!!.setVisibility(View.GONE)
        }
    }

    private fun initList() {
        var musicAdapter = MusicAdapter(
            ConstantClass.musicList,
            context,
            object : MusicAdapter.SetOnClickListener {
                override fun onFavClick() {
                    setOnClickListener!!.onFavClick()
                }

                override fun onPlayClick() {
                    startActivity(Intent(context, PlayActivity::class.java))
                }
            })
        rv_view!!.setHasFixedSize(true)
        rv_view!!.layoutManager = LinearLayoutManager(context)
        rv_view!!.adapter = musicAdapter
    }

    fun setInterface(setOnClickListener: SetOnClickListener) {
        this.setOnClickListener = setOnClickListener
    }

    interface SetOnClickListener {
        fun onFavClick()
    }

}