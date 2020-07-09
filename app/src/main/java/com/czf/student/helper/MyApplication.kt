package com.czf.student.helper

import android.app.Application
import android.content.Context

object MyApplication: Application() {
    private var context:Context? = null

    override fun onCreate() {
        super.onCreate()
        context=applicationContext
    }

    @JvmStatic
    fun getContext():Context{
        return context!!
    }
}