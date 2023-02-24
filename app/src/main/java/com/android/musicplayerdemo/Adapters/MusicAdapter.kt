package com.example.musicplayer.Adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.Activity.PlayActivity
import com.example.musicplayer.ConstantClass
import com.example.musicplayer.R
import com.example.musicplayer.models.Audio
import com.bumptech.glide.Glide

class MusicAdapter(
    private val musicList: ArrayList<Audio>,
    private val context: Context?,
    private var setOnClickListener: SetOnClickListener
) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.song_list_layout, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val singleMusicFile = musicList.get(position)
        holder.songName.text = singleMusicFile.title
        if (singleMusicFile.thumbnail != null)
            Glide.with(context!!).asBitmap().load(singleMusicFile.thumbnail!!).into(holder.musicImg)
        holder.itemView.setOnClickListener {
            ConstantClass.currentListIndex = position
            context?.startActivity(Intent(context, PlayActivity::class.java))
        }
        holder.more_btn.setOnClickListener(View.OnClickListener { //creating a popup menu
            val popup = PopupMenu(context, holder.more_btn)

            popup.inflate(R.menu.options_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.play_ -> {
                        setOnClickListener.onPlayClick()
                    }
                    R.id.fav_ -> {
                        setOnClickListener.onFavClick()
                    }
                }
                false
            }
            popup.show()
        })
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songName: TextView = itemView.findViewById(R.id.SongName)
        var musicImg: ImageView = itemView.findViewById(R.id.music_img)
        var more_btn: ImageView = itemView.findViewById(R.id.more_btn)
    }

    interface SetOnClickListener {
        fun onFavClick()
        fun onPlayClick()
    }
}