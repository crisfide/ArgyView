package com.markets.argyview

import android.app.Application
import android.os.Build
import com.jakewharton.threetenabp.AndroidThreeTen

class ArgyView : Application() {

    override fun onCreate() {
        super.onCreate()

        //if (Build.VERSION.SDK_INT < 26) {
            AndroidThreeTen.init(this)
        //}
    }
}