package com.example.musicplayer.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.Adapters.FavAdapter
import com.example.musicplayer.Database.FavSongDatabase.Companion.db
import com.example.musicplayer.R


class FavFragment : Fragment() {
    private var iv_not_available: ImageView? = null
    private var roomadapter: FavAdapter? = null
    private var rv_v: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        rv_v = view.findViewById(R.id.rv_v)
        iv_not_available = view.findViewById(R.id.no_data)
        return view
    }

    override fun onResume() {
        super.onResume()
        initList()
    }

    private fun initList() {
        if (db!!.favsongDao().getAll() != null && db!!.favsongDao().getAll().size != 0) {
            iv_not_available!!.setVisibility(View.GONE)
            rv_v!!.setVisibility(View.VISIBLE)

            val linearLayoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            roomadapter = FavAdapter(requireContext(), db!!.favsongDao().getAll())
            rv_v!!.layoutManager = linearLayoutManager
            rv_v!!.adapter = roomadapter
        } else {
            iv_not_available!!.setVisibility(View.VISIBLE)
            rv_v!!.setVisibility(View.GONE)
        }
    }

}