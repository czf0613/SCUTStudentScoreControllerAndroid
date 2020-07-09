package com.czf.student.helper

import android.app.Application
import android.content.Context

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        context=applicationContext
    }

    companion object{
        private var context:Context? = null

        @JvmStatic
        fun getContext(): Context {
            return context!!
        }
    }
}