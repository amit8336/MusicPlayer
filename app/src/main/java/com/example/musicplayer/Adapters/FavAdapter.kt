package com.example.musicplayer.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.Activity.FavSongPlayActivity
import com.example.musicplayer.ConstantClass
import com.example.musicplayer.R
import com.example.musicplayer.models.FavSongModel

class FavAdapter(private val context: Context, private var favList: List<FavSongModel>) :
    RecyclerView.Adapter<FavAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fav_music_img = itemView.findViewById<ImageView>(R.id.fav_music_img)
        val fav_SongName = itemView.findViewById<TextView>(R.id.fav_SongName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemview =
            LayoutInflater.from(parent.context).inflate(R.layout.fav_list_layout, parent, false)
        return ViewHolder(itemview)
    }

    override fun getItemCount(): Int {
        return favList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favmusic = favList.get(position)
        holder.fav_SongName.text = favmusic.title

        ConstantClass.currentListIndex = position
        holder.itemView.setOnClickListener(View.OnClickListener {
            context?.startActivity(
                Intent(context, FavSongPlayActivity::class.java).putExtra(
                    "pos",
                    position
                )
            )
        })
    }
}