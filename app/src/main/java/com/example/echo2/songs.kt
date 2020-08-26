package com.example.echo2

import android.os.Parcel
import android.os.Parcelable

class songs(
    var songid: Long,
    var songTitle: String,
    var artist: String,
    var songdata: String,
    var dateadded: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    object Staticated {
        var nameComparator: Comparator<songs> = Comparator<songs> { song1, song2 ->
            val songone = song1.songTitle.toUpperCase()
            val songtwo = song2.songTitle.toUpperCase()
            songone.compareTo(songtwo)


        }
        var dataComparator: Comparator<songs> = Comparator<songs> { song1, song2 ->
            val songone = song1.dateadded.toDouble()
            val songtwo = song2.dateadded.toDouble()
            songone.compareTo(songtwo)
        }
    }

    companion object CREATOR : Parcelable.Creator<songs> {
        override fun createFromParcel(parcel: Parcel): songs {
            return songs(parcel)
        }

        override fun newArray(size: Int): Array<songs?> {
            return arrayOfNulls(size)
        }
    }


}


