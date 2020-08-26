package com.example.echo2.activities

import android.app.Notification
import android.app.Notification.Builder
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.echo2.Adapter.NavigationDrawerAdapter
import com.example.echo2.R
import com.example.echo2.activities.MainActivity.Statified.drawerlayout
import com.example.echo2.fragment.MainScreenFragment
import com.example.echo2.fragment.SongOnPlaying
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    //object deeclaration

    var iconname: ArrayList<String> = arrayListOf()
    var icons: IntArray = intArrayOf(
        R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites,
        R.drawable.navigation_settings,
        R.drawable.navigation_aboutus
    )
    var tracknotificationbuilder: Notification? = null

    object Statified {
        var drawerlayout: DrawerLayout? = null
        var notificationmanager: NotificationManager? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerlayout = findViewById(R.id.drawer_layout)

        iconname.add("All Songs")
        iconname.add("Favorites")
        iconname.add("Settings")
        iconname.add("About us ")

        val toggle = ActionBarDrawerToggle(
            this@MainActivity, drawerlayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerlayout?.setDrawerListener(toggle)
        toggle.syncState()


        val mainScreenFragment = MainScreenFragment()
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_details, mainScreenFragment, "MainscreenFragment")
            .commit()

        var _navigationAdapter = NavigationDrawerAdapter(iconname, icons, this)
        _navigationAdapter.notifyDataSetChanged()
        var navigation_recycler_view = findViewById<RecyclerView>(R.id.navigationrecyclerview)
        navigation_recycler_view.layoutManager = LinearLayoutManager(this);
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
        navigation_recycler_view.adapter = _navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)
        val intent = Intent(this, MainActivity::class.java)
        val pintent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(),
            intent, 0
        )
        tracknotificationbuilder = Notification.Builder(this)
            .setContentTitle("A track is playing in the background ")
            .setSmallIcon(R.drawable.echo_logo)
            .setContentIntent(pintent)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()

        Statified.notificationmanager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    }

    override fun onStart() {
        super.onStart()
        try {
            Statified.notificationmanager?.cancel(1978)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if (SongOnPlaying.Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.notificationmanager?.notify(1978, tracknotificationbuilder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
      try {
            Statified.notificationmanager?.cancel(1978)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
