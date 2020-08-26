package com.example.echo2.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.echo2.databases.EchoDatabase.Staticated.COLUMN_ID
import com.example.echo2.databases.EchoDatabase.Staticated.COLUMN_PATH
import com.example.echo2.databases.EchoDatabase.Staticated.COLUMN_SONG_ARTIST
import com.example.echo2.databases.EchoDatabase.Staticated.COLUMN_SONG_TITLE
import com.example.echo2.databases.EchoDatabase.Staticated.TABLE_NAME
import com.example.echo2.songs
import java.lang.Exception

class EchoDatabase : SQLiteOpenHelper {


    object Staticated {
        var DB_VERSION = 1
        val DB_NAME = "FavouriteDatabase"
        val TABLE_NAME = "FavouriteTable"
        val COLUMN_ID = "SongId"
        val COLUMN_SONG_TITLE = "SongTitle"
        val COLUMN_SONG_ARTIST = "SOngArtist"
        val COLUMN_PATH = "SongPath"
    }

    var _songlist = ArrayList<songs>()

    constructor(
        context: Context?,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int
    ) : super(context, name, factory, version)

    constructor(
        context: Context?
    ) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)

    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase?.execSQL(
            "CREATE TABLE " + Staticated.TABLE_NAME + "( " + Staticated.COLUMN_ID + " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " STRING," + Staticated.COLUMN_SONG_TITLE + " STRING," + Staticated.COLUMN_PATH + "STRING);"
        )


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun storeasfavourite(id: Int?, artist: String?, songTitle: String?, path: String?) {
        val db = this.writableDatabase
        var contentvalues = ContentValues()
        contentvalues.put(Staticated.COLUMN_ID, id)
        contentvalues.put(Staticated.COLUMN_SONG_ARTIST, artist)
        contentvalues.put(Staticated.COLUMN_SONG_TITLE, songTitle)
        contentvalues.put(Staticated.COLUMN_PATH, path)
        db.insert(Staticated.TABLE_NAME, null, contentvalues)
        db.close()
    }

    fun queryDBliat(): ArrayList<songs>? {
        try {
            val db = this.readableDatabase
            val query_parms = "select * from " + Staticated.TABLE_NAME
            var csor = db.rawQuery(query_parms, null)
            if (csor.moveToFirst()) {
                do {

                    var _id = csor.getInt(csor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist =
                        csor.getString(csor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _title =
                        csor.getString(csor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _path = csor.getString(csor.getColumnIndexOrThrow(Staticated.COLUMN_PATH))
                    _songlist.add(songs(_id.toLong(), _title, _artist, _path, 0))


                } while (csor.moveToNext())
            } else
                return null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return _songlist
    }

    fun checkidexists(_id: Int): Boolean? {
        var storeid = -1090
        val db = this.readableDatabase
        val query_parms = "Select * from " + TABLE_NAME + " where SongId = '$_id'"
        val csor = db.rawQuery(query_parms, null)
        if (csor.moveToFirst()) {
            do {
                storeid = csor.getInt(csor.getColumnIndexOrThrow(COLUMN_ID))

            } while (csor.moveToNext())
        } /*else {
            return false
        }*/
        return storeid != -1090
    }

    fun deletefavourite(_id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, COLUMN_ID + "=" + _id, null)
        db.close()
    }

    fun checksize(): Int {
        var count = 0
        val db = this.readableDatabase
        val query_parms = "select * from " + Staticated.TABLE_NAME
        var csor = db.rawQuery(query_parms, null)
        if (csor.moveToFirst()) {
            do {
                count += 1
            } while (csor.moveToNext())
        } else {
            return 0
        }
        return count

    }

}