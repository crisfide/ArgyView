package com.markets.argyview

import android.app.Application
//import androidx.room.Room
import com.jakewharton.threetenabp.AndroidThreeTen
//import com.markets.argyview.data.db.ActivosDB

class ArgyView : Application() {

    /*val room = Room
        .databaseBuilder(this, ActivosDB::class.java, "activosDB")
        .build()*/

    override fun onCreate() {
        super.onCreate()



        //if (Build.VERSION.SDK_INT < 26) {
            AndroidThreeTen.init(this)
        //}
    }
}