package com.example.echo2.fragment


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.echo2.Adapter.FavouriteAdapter
import com.example.echo2.R
import com.example.echo2.databases.EchoDatabase
import com.example.echo2.songs
import kotlinx.android.synthetic.main.fragment_faviourate.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class Faviourate : Fragment() {
    var myactivity: Activity? = null
    var noFavourites: TextView? = null
    var nowPlayinbottombar: RelativeLayout? = null
    var playpausebtn: ImageButton? = null
    var songtitle: TextView? = null
    var recyclerview: RecyclerView? = null
    var trackposition: Int = 0
    var favcontent: EchoDatabase? = null
    var refreshlist: ArrayList<songs>? = null
    var getlistdb: ArrayList<songs>? = null

    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faviourate, container, false)
        activity?.title = "Favorites"
        noFavourites = view?.findViewById(R.id.nofavouites)
        nowPlayinbottombar = view?.findViewById(R.id.hiddenfavlayout)
        songtitle = view?.findViewById(R.id.songtitle)
        playpausebtn = view?.findViewById(R.id.playpausebtn)
        recyclerview = view?.findViewById(R.id.favouriterecycler)
        return view

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favcontent = EchoDatabase(myactivity)
        display_fav_by_searching()
        bottombarsetup()


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    private fun getsongsfromphone(): ArrayList<songs> {
        var arraylist = ArrayList<songs>()
        var contentresolver = myactivity?.contentResolver
        var songuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songcursor = contentresolver?.query(songuri, null, null, null, null)
        if (songcursor != null && songcursor.moveToFirst()) {
            val songid = songcursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songtitle = songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songartist = songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songdata = songcursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateindex = songcursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songcursor.moveToNext()) {
                var cid = songcursor.getLong(songid)
                var ctitle = songcursor.getString(songtitle)
                var cartist = songcursor.getString(songartist)
                var cdata = songcursor.getString(songdata)
                var cdate = songcursor.getLong(dateindex)

                arraylist.add(songs(cid, ctitle, cartist, cdata, cdate))
            }
        }
        return arraylist
    }


    fun display_fav_by_searching() {
        if (favcontent?.checksize() as Int > 0) {
            refreshlist = ArrayList<songs>()
            getlistdb = favcontent?.queryDBliat()
            var fetchlistfromdevice = getsongsfromphone()
            if (fetchlistfromdevice != null) {
                for (i in 0..fetchlistfromdevice?.size - 1) {
                    for (j in 0..getlistdb?.size as Int - 1) {
                        if ((getlistdb?.get(j)?.songid) == (fetchlistfromdevice?.get(i)?.songid)) {
                            refreshlist?.add((getlistdb as ArrayList<songs>)[j])
                        }
                    }
                }
            } else {

            }
            if (refreshlist == null) {
                recyclerview?.visibility = View.INVISIBLE
                noFavourites?.visibility = View.VISIBLE
            } else {
                var favadapter =
                    FavouriteAdapter(refreshlist as ArrayList<songs>, myactivity as Context)
                var mlayoutmanager = LinearLayoutManager(activity)
                recyclerview?.layoutManager = mlayoutmanager
                recyclerview?.itemAnimator = DefaultItemAnimator()
                recyclerview?.adapter = favadapter
                recyclerview?.setHasFixedSize(true)


            }
        } else {
            recyclerview?.visibility = View.INVISIBLE
            noFavourites?.visibility = View.VISIBLE
        }

    }

    fun bottombarclickhandler() {
        nowPlayinbottombar?.setOnClickListener({
            Statified.mediaPlayer = SongOnPlaying.Statified.mediaPlayer
            val SongonPlaying = SongOnPlaying()
            var args = Bundle()
            args.putString("songartist", SongOnPlaying.Statified.currentSongHelper?.songArtist)
            args.putString("path", SongOnPlaying.Statified.currentSongHelper?.songPath)
            args.putString("songtitle", SongOnPlaying.Statified.currentSongHelper?.songTitle)
            args.putInt("id", SongOnPlaying.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt(
                "position",
                SongOnPlaying.Statified.currentSongHelper?.currentPosition?.toInt() as Int
            )
            args.putParcelableArrayList("songData", SongOnPlaying.Statified.fetchSongs)
            args.putString("favbottombar", "sucess")
            SongonPlaying.arguments = args


            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_details, SongonPlaying)
                ?.addToBackStack("songPlayingfragment")
                ?.commit()
        })

        playpausebtn?.setOnClickListener(
            {
                if (SongOnPlaying.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongOnPlaying.Statified.mediaPlayer?.pause()
                    trackposition = SongOnPlaying.Statified.mediaPlayer?.getCurrentPosition() as Int
                    playpausebtn?.setBackgroundResource(R.drawable.play_icon)

                } else {
                    SongOnPlaying.Statified.mediaPlayer?.seekTo(trackposition)
                    SongOnPlaying.Statified.mediaPlayer?.start()
                    playpausebtn?.setBackgroundResource(R.drawable.pause_icon)
                }
            }

        )
    }

    fun bottombarsetup() {
        try {
            bottombarclickhandler()
            songtitle?.setText(SongOnPlaying.Statified.currentSongHelper?.songTitle)
            SongOnPlaying.Statified.mediaPlayer?.setOnCompletionListener {
                {
                    songtitle?.setText(SongOnPlaying.Statified.currentSongHelper?.songTitle)
                    SongOnPlaying.Staticated.onSongComplete()
                }
            }
            if (SongOnPlaying.Statified.mediaPlayer?.isPlaying as Boolean) {
                hiddenfavlayout?.visibility = View.VISIBLE
            } else {
                nowPlayinbottombar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
