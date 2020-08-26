package com.example.echo2.fragment


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.echo2.CurrentSongHelper
import com.example.echo2.R
import com.example.echo2.activities.MainActivity
import com.example.echo2.databases.EchoDatabase
import com.example.echo2.songs
import com.example.echo2.utills.SeekBarController
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class SongOnPlaying : Fragment() {
    object Statified {
        var myactivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var back: String? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var check: Boolean = true
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var currentSongHelper: CurrentSongHelper? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<songs>? = null
        var audioVisualization: AudioVisualization? = null
        var glview: GLAudioVisualizationView? = null
        var fab: ImageButton? = null
        var favouritecontet: EchoDatabase? = null
        var msensormanager: SensorManager? = null
        var msensorlistener: SensorEventListener? = null
        var MY_PREFS_NAME = "shake feature"
        var updatesongtime = object : Runnable {
            override fun run() {
                try {


                    val getCurrent = Statified.mediaPlayer?.getCurrentPosition()
                    Statified.startTimeText?.setText(
                        String.format(
                            "%d:%d", TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                            TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong() as Long) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long)
                            )
                        )
                    )
                    Statified.seekbar?.setProgress(getCurrent?.toInt() as Int)
                    Handler().postDelayed(this, 1000)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

        }


    }

    object Staticated {
        var MY_PREFS_SHUFFLE = "shuffle feature"
        var MY_PREFS_LOOP = "loop feature"

        fun onSongComplete() {
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            } else {
                if (Statified.currentSongHelper?.isloop as Boolean) {
                    Statified.currentSongHelper?.isPlaying = true
                    var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
                    Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                    Statified.currentSongHelper?.songPath = nextSong?.songdata
                    Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.songArtist = nextSong?.artist
                    Statified.currentSongHelper?.songId = nextSong?.songid as Long
                    updatetextviews(
                        Statified.currentSongHelper?.songTitle as String,
                        Statified.currentSongHelper?.songArtist as String
                    )
                    Statified.mediaPlayer?.reset()
                    try {
                        Statified.mediaPlayer?.setDataSource(
                            Statified.myactivity as Context,
                            Uri.parse(Statified.currentSongHelper?.songPath)
                        )
                        Statified.mediaPlayer?.prepare()
                        Statified.mediaPlayer?.start()
                        processInformation(Statified.mediaPlayer as MediaPlayer)


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }
            if (Statified.favouritecontet?.checkidexists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myactivity!!,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myactivity!!,
                        R.drawable.favorite_off
                    )
                )
            }
        }

        fun updatetextviews(songtitle: String, songartist: String) {
            var songtitleupdated = songtitle
            if (songtitle.equals("<unknown>", true)) {
                songtitleupdated = "Unknown"
            }
            var songrtistupdated = songartist
            if (songartist.equals("<unknown>", true)) {
                songrtistupdated = "Unknown"
            }
            Statified.songTitleView?.setText(songtitle)
            Statified.songArtistView?.setText(songartist)
        }

        fun processInformation(mediaplayer: MediaPlayer) {
            val finaltime = mediaplayer.duration
            val starttime = mediaplayer.currentPosition
            Statified.seekbar?.max = finaltime
            Statified.startTimeText?.setText(
                String.format(
                    "%d:%d", TimeUnit.MILLISECONDS.toMinutes(starttime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(starttime.toLong()) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(starttime.toLong())
                    )
                )
            )
            Statified.endTimeText?.setText(
                String.format(
                    "%d:%d", TimeUnit.MILLISECONDS.toMinutes(finaltime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finaltime.toLong()) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(finaltime.toLong())
                    )
                )
            )
            Statified.seekbar?.setProgress(starttime)
            Handler().postDelayed(Statified.updatesongtime, 1000)
        }

        fun playNext(check: String) {
            if (Statified.currentSongHelper?.isPlaying as Boolean) {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            if (check.equals("PlayNextNormal", true)) {
                Statified.currentPosition += 1
            } else if (check.equals("PlayNextNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition =
                    randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified.currentPosition = randomPosition

            }
            if (Statified.currentPosition == Statified.fetchSongs?.size) {
                Statified.currentPosition = 0
            }
            Statified.currentSongHelper?.isloop = false
            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songdata
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songId = nextSong?.songid as Long
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition   //line aded
            //lined addded from here
            var editorLoop = Statified.myactivity?.getSharedPreferences(
                Staticated.MY_PREFS_LOOP,
                Context.MODE_PRIVATE
            )?.edit()

            Statified.currentSongHelper?.isloop = false
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            editorLoop?.putBoolean("feature", false)
            editorLoop?.apply()
            //to here
            updatetextviews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )
            Statified.mediaPlayer?.reset()
            try {
                Statified.mediaPlayer?.setDataSource(
                    Statified.myactivity as Context,
                    Uri.parse(Statified.currentSongHelper?.songPath)
                )
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
                processInformation(Statified.mediaPlayer as MediaPlayer)


            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Statified.favouritecontet?.checkidexists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myactivity!!,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myactivity!!,
                        R.drawable.favorite_off
                    )
                )
            }
        }

        fun playpervious() {
            Statified.currentPosition -= 1
            if (Statified.currentPosition == -1)
                Statified.currentPosition = 0
            if (Statified.currentSongHelper?.isPlaying as Boolean) {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            Statified.currentSongHelper?.isloop = false
            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songdata
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.artist
            Statified.currentSongHelper?.songId = nextSong?.songid as Long
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition//added gere
            Staticated.updatetextviews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )
            Statified.mediaPlayer?.reset()
            try {
                Statified.mediaPlayer?.setDataSource(
                    Statified.myactivity as Context,
                    Uri.parse(Statified.currentSongHelper?.songPath)
                )
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                Staticated.processInformation(Statified.mediaPlayer as MediaPlayer) //here
                Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)


            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Statified.favouritecontet?.checkidexists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myactivity!!,
                        R.drawable.favorite_on
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myactivity!!,
                        R.drawable.favorite_off
                    )
                )
            }

        }

    }


    var macceleration: Float = 0f
    var maccelerationCurrent: Float = 0f
    var maccelerationlast: Float = 0f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_song_on_playing, container, false)
        setHasOptionsMenu(true)
        activity?.title = "Now Playing"

        Statified.seekbar = view?.findViewById(R.id.seekbar)
        Statified.startTimeText = view?.findViewById(R.id.starttime)
        Statified.endTimeText = view?.findViewById(R.id.endtime)
        Statified.playPauseImageButton = view?.findViewById(R.id.playpausebutton)
        Statified.previousImageButton = view?.findViewById(R.id.previousbutton)
        Statified.nextImageButton = view?.findViewById(R.id.nextbutton)
        Statified.loopImageButton = view?.findViewById(R.id.loopbutton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shufflebutton)
        Statified.songArtistView = view?.findViewById(R.id.songartist)
        Statified.songTitleView = view?.findViewById(R.id.song_titkle)
        Statified.glview = view?.findViewById(R.id.visualizer_view)
        Statified.fab = view?.findViewById(R.id.favicon)
        Statified.fab?.alpha = 0.8f
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization = Statified.glview as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myactivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
        Statified.msensormanager?.registerListener(
            Statified.msensorlistener, Statified.msensormanager?.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {

        Statified.audioVisualization?.onPause()
        super.onPause()

        Statified.msensormanager?.unregisterListener(Statified.msensorlistener)

    }

    override fun onDestroy() {
        super.onDestroy()
        Statified.mediaPlayer?.stop()//here
        Statified.audioVisualization?.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.msensormanager =
            Statified.myactivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        macceleration = 0.0f
        maccelerationCurrent = SensorManager.GRAVITY_EARTH
        maccelerationlast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_redirect -> {
                var pos = 1
                if (Statified.back.equals("MainScreen", true)) {
                    pos = 1
                }

                if (Statified.back.equals("Favorite", true)) {
                    pos = 0
                }

                if (pos == 1) {
                    val mainScreenFragment = MainScreenFragment()
                    (context as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_details, mainScreenFragment)
                        .addToBackStack("MainFragment")
                        .commit()
                }

                /*The next item is the Favorites option and the fragment corresponding to it is the favorite fragment at position 1*/
                if (pos == 0) {
                    val favoriteFragment = Faviourate()
                    (context as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_details, favoriteFragment)
                        .addToBackStack("FavoriteFragment")
                        .commit()
                }
                return false
            }

        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isShuffle = false
        Statified.currentSongHelper?.isloop = false
        Statified.favouritecontet = EchoDatabase(Statified.myactivity)

        var path: String? = null
        var _songtitle: String? = null
        var _songartist: String? = null
        var _songid: Long = 0
        try {
            path = arguments?.getString("path")
            _songartist = arguments?.getString("songartist")
            _songid = arguments?.getInt("id")?.toLong()!!//songid
            _songtitle = arguments?.getString("songtitle")
            Statified.currentPosition = arguments?.getInt(" position")!!//songPosition
            Statified.fetchSongs = arguments?.getParcelableArrayList("songData")

            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songArtist = _songartist
            Statified.currentSongHelper?.songTitle = _songtitle
            Statified.currentSongHelper?.songId = _songid
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Staticated.updatetextviews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromfavBottombar = arguments?.get("FavBottombar") as? String
        if (fromfavBottombar != null) {
            Statified.mediaPlayer = Faviourate.Statified.mediaPlayer
        } else {
            Statified.mediaPlayer = MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaPlayer?.setDataSource(
                    Statified.myactivity as Context,
                    Uri.parse(path)
                )
                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Statified.mediaPlayer?.start()
        }

        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }
        clickHandler()
        var visualizationHandler =
            DbmHandler.Factory.newVisualizerHandler(Statified.myactivity as Context, 0)
        Statified.audioVisualization?.linkTo(visualizationHandler)
        var prefsforshuffle =
            Statified.myactivity?.getSharedPreferences(
                Staticated.MY_PREFS_SHUFFLE,
                Context.MODE_PRIVATE
            )
        var ishuffleallowed = prefsforshuffle?.getBoolean("feature", false)
        if (ishuffleallowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isloop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

        } else {
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var prefsforloop =
            Statified.myactivity?.getSharedPreferences(
                Staticated.MY_PREFS_LOOP,
                Context.MODE_PRIVATE
            )
        var isloopallowed = prefsforloop?.getBoolean("feature", false)
        if (isloopallowed as Boolean) {
            Statified.currentSongHelper?.isShuffle = false
            Statified.currentSongHelper?.isloop = true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)

        } else {
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        if ((Statified?.favouritecontet?.checkidexists(Statified?.currentSongHelper?.songId?.toInt() as Int) as Boolean)) {
            Statified.fab?.setImageDrawable(
                ContextCompat.getDrawable(
                    Statified.myactivity!!,
                    R.drawable.favorite_on
                )
            )
        } else {
            Statified.fab?.setImageDrawable(
                ContextCompat.getDrawable(
                    Statified.myactivity!!,
                    R.drawable.favorite_off
                )
            )
        }
        seekbarHandler()
    }


    fun clickHandler() {
        Statified.fab?.setOnClickListener({
            if (Statified.favouritecontet?.checkidexists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myactivity!!,
                        R.drawable.favorite_off
                    )
                )
                Statified.favouritecontet?.deletefavourite(Statified.currentSongHelper?.songId as Int)
                Toast.makeText(Statified.myactivity, "Removed from favorites", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        Statified.myactivity!!,
                        R.drawable.favorite_on
                    )
                )
                Statified.favouritecontet?.storeasfavourite(
                    Statified.currentSongHelper?.songId?.toInt(),
                    Statified.currentSongHelper?.songArtist,
                    Statified.currentSongHelper?.songTitle,
                    Statified.currentSongHelper?.songPath
                )
                Toast.makeText(Statified.myactivity, "Added to favorites", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        Statified.shuffleImageButton?.setOnClickListener({
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            var editorshuffle =
                Statified.myactivity?.getSharedPreferences(
                    Staticated.MY_PREFS_SHUFFLE,
                    Context.MODE_PRIVATE
                )
                    ?.edit()
            var editorloop =
                Statified.myactivity?.getSharedPreferences(
                    Staticated.MY_PREFS_LOOP,
                    Context.MODE_PRIVATE
                )
                    ?.edit()

            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statified.currentSongHelper?.isShuffle = false
                editorshuffle?.putBoolean("feature", false)
                editorshuffle?.apply()
            } else {
                Statified.currentSongHelper?.isShuffle = true
                Statified.currentSongHelper?.isloop = false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorshuffle?.putBoolean("feature", true)
                editorshuffle?.apply()
                editorloop?.putBoolean("feature", false)
                editorloop?.apply()
            }
        })
        Statified.nextImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isloop as Boolean) {
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Staticated.playNext("PlayNextNormalShuffle")
            } else {
                Staticated.playNext("PlayNextNormal")
            }
        })
        Statified.previousImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isloop as Boolean) {
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            Staticated.playpervious()

        })
        Statified.loopImageButton?.setOnClickListener({
            var editorshuffle =
                Statified.myactivity?.getSharedPreferences(
                    Staticated.MY_PREFS_SHUFFLE,
                    Context.MODE_PRIVATE
                )
                    ?.edit()
            var editorloop =
                Statified.myactivity?.getSharedPreferences(
                    Staticated.MY_PREFS_LOOP,
                    Context.MODE_PRIVATE
                )
                    ?.edit()

            if (Statified.currentSongHelper?.isloop as Boolean) {
                Statified.currentSongHelper?.isloop = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorloop?.putBoolean("feature", false)
                editorloop?.apply()
            } else {
                Statified.currentSongHelper?.isloop = true
                Statified.currentSongHelper?.isShuffle = false
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorshuffle?.putBoolean("feature", false)
                editorshuffle?.apply()
                editorloop?.putBoolean("feature", true)
                editorloop?.apply()
            }
        })
        Statified.playPauseImageButton?.setOnClickListener({
            if (Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.mediaPlayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                Statified.mediaPlayer?.start()
                Statified.currentSongHelper?.isPlaying = false
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }


    fun bindShakeListener() {

        Statified.msensorlistener = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

            override fun onSensorChanged(p0: SensorEvent) {

                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]

                maccelerationlast = maccelerationCurrent
                maccelerationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()
                val delta = maccelerationCurrent - maccelerationlast
                macceleration = macceleration * 0.9f + delta
                if (macceleration > 12) {
                    println("11111")
                    val prefs = Statified.myactivity?.getSharedPreferences(
                        Statified.MY_PREFS_NAME,
                        Context.MODE_PRIVATE
                    )
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean && Statified.check == true) {
                        Statified.currentSongHelper?.isPlaying = true
                        if (Statified.currentSongHelper?.isloop as Boolean) {
                            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                        }
                        if (Statified.currentSongHelper?.isShuffle as Boolean) {
                            Staticated.playNext("PlayNextLikeNormalShuffle")
                        } else {
                            Staticated.playNext("PlayNextNormal")
                        }
                        Statified.check = false
                    }
                }
            }

        }
    }

    fun seekbarHandler() {
        val seekbarListener = SeekBarController()
        Statified.seekbar?.setOnSeekBarChangeListener(seekbarListener)
    }


}


