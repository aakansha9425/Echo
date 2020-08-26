package com.example.echo2.fragment


import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.echo2.Adapter.MainScreenAdapter
import com.example.echo2.R
import com.example.echo2.songs
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MainScreenFragment : Fragment() {
    //variable declaration .....
    var visiblelayout: RelativeLayout? = null
    var songlist: RecyclerView? = null
    var hiddenlayout: RelativeLayout? = null
    var playpausebtn: ImageButton? = null
    var defaultmusic: ImageView? = null
    var nowplaying: TextView? = null
    var songtitle: TextView? = null
    var trackposition: Int = 0
    var nosongs: RelativeLayout? = null
    var presentnosongs: TextView? = null
    var myactivity: Activity? = null
    var getsonglist: ArrayList<songs>? = null
    var _mainscreenadapter: MainScreenAdapter? = null

    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
        visiblelayout = view?.findViewById(R.id.visiblelayout)
        songlist = view?.findViewById(R.id.songslist)
        hiddenlayout = view?.findViewById(R.id.hiddenlayout)
        playpausebtn = view?.findViewById(R.id.playpausebtn)
        defaultmusic = view?.findViewById(R.id.defaultmusic)
        nowplaying = view?.findViewById(R.id.nowplaying)
        songtitle = view?.findViewById(R.id.songtitle)
        nosongs = view?.findViewById(R.id.nosongs)
        presentnosongs = view?.findViewById(R.id.presentnosongs)
        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getsonglist = getsongsfromphone()
        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")
        if (getsonglist == null) {
            visiblelayout?.visibility = View.INVISIBLE
            nosongs?.visibility = View.VISIBLE
        } else {
            _mainscreenadapter =
                MainScreenAdapter(getsonglist as ArrayList<songs>, myactivity as Context)
            val mlayoutmanager = LinearLayoutManager(myactivity)
            songlist?.layoutManager = mlayoutmanager
            songlist?.itemAnimator = DefaultItemAnimator()
            songlist?.adapter = _mainscreenadapter
        }
        if (getsonglist != null) {
            if (action_sort_ascending!!.equals("true", true)) {
                Collections.sort(getsonglist, songs.Staticated.nameComparator)
                _mainscreenadapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", true)) {
                Collections.sort(getsonglist, songs.Staticated.dataComparator)
                _mainscreenadapter?.notifyDataSetChanged()
            }
        }
        bottombarsetup()

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.action_sort_name) {
            val editor =
                myactivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()

            if (getsonglist != null) {
                Collections.sort(getsonglist, songs.Staticated.nameComparator)

            }
            _mainscreenadapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_recent) {
            if (getsonglist != null) {
                Collections.sort(getsonglist, songs.Staticated.dataComparator)

            }
            _mainscreenadapter?.notifyDataSetChanged()
            return false

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
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

    fun bottombarclickhandler() {
        hiddenlayout?.setOnClickListener({
            MainScreenFragment.Statified.mediaPlayer = SongOnPlaying.Statified.mediaPlayer
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
                hiddenlayout?.visibility = View.VISIBLE
            } else {
                hiddenlayout?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
