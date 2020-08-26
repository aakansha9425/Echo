package com.example.echo2.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.echo2.R
import com.example.echo2.fragment.SongOnPlaying
import com.example.echo2.songs

class FavouriteAdapter(_songDetails: ArrayList<songs>, _context: Context) :
    RecyclerView.Adapter<FavouriteAdapter.MyviewHolder>() {

    var songdetails: ArrayList<songs>? = null
    var mcontext: Context? = null

    init {
        this.songdetails = _songDetails
        this.mcontext = _context
    }

    class MyviewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tracktitle: TextView? = null
        var trackartist: TextView? = null
        var contentlayout: RelativeLayout? = null

        init {
            trackartist = view.findViewById(R.id.songartist)
            tracktitle = view.findViewById(R.id.tracktitle)
            contentlayout = view.findViewById(R.id.songlistmainlayout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        var itemview = LayoutInflater.from(parent?.context)
            .inflate(R.layout.row_custom_navigation_drawer, parent, false)
        val returnThis = MyviewHolder(itemview)
        return returnThis
    }

    override fun getItemCount(): Int {
        if (songdetails == null)
            return 0
        else {
            return (songdetails as ArrayList<songs>).size
        }
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
        val songobject = songdetails?.get(position)
        holder.tracktitle?.text = songobject?.songTitle
        holder.trackartist?.text = songobject?.artist
        holder.contentlayout?.setOnClickListener({
            // Toast.makeText(mcontext, "hey" + songobject?.songTitle, Toast.LENGTH_SHORT).show()

            val SongonPlaying = SongOnPlaying()
            var args = Bundle()
            args.putString("songartist", songobject?.artist)
            args.putString("path", songobject?.songdata)
            args.putString("songtitle", songobject?.songTitle)
            args.putInt("id", songobject?.songid?.toInt() as Int)
            args.putInt("position", position)
            args.putParcelableArrayList("songData", songdetails)
            SongonPlaying.arguments = args
            if (SongOnPlaying.Statified.mediaPlayer != null && SongOnPlaying.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongOnPlaying.Statified.mediaPlayer?.pause()
                SongOnPlaying.Statified.mediaPlayer?.release()
            }
            (mcontext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_details, SongonPlaying, "SongPlayingFragment")
                .addToBackStack("SongPlayingFragmentFav")
                .commit()


            (mcontext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_details, SongonPlaying)
                .addToBackStack("songplayingfragmentfavourite")
                .commit()

        })
    }
}