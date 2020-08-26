package com.example.echo2.utills

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.TelephonyManager
import com.example.echo2.R
import com.example.echo2.activities.MainActivity
import com.example.echo2.fragment.SongOnPlaying
import java.lang.Exception

class capturebroadcast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.Statified.notificationmanager?.cancel(1978)
            }catch (e:Exception)
            {
                e.printStackTrace()
            }
            try {
                if (SongOnPlaying.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongOnPlaying.Statified.mediaPlayer?.pause()
                    SongOnPlaying.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val tm: TelephonyManager =
                p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (tm.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    try {
                        MainActivity.Statified.notificationmanager?.cancel(1978)
                    }catch (e:Exception)
                    {
                        e.printStackTrace()
                    }
                    try {
                        if (SongOnPlaying.Statified.mediaPlayer?.isPlaying as Boolean) {
                            SongOnPlaying.Statified.mediaPlayer?.pause()
                            SongOnPlaying.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        }
    }

}