package com.example.echo2.utills

import android.widget.SeekBar
import com.example.echo2.fragment.SongOnPlaying


class SeekBarController : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        if (SongOnPlaying.Statified.mediaPlayer == null)
            return
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        if (p0?.progress!! < SongOnPlaying.Statified.mediaPlayer!!.duration) {
            SongOnPlaying.Statified.mediaPlayer!!.seekTo(p0?.progress!!)
        } else {
            SongOnPlaying.Statified.mediaPlayer?.seekTo((SongOnPlaying.Statified.mediaPlayer?.duration)!!.toInt())
        }
    }
}