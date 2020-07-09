package com.czf.student.helper

import android.content.Context
import java.lang.Exception

object StringResourceGetter {
    private val context: Context = MyApplication.getContext()

    @JvmStatic
    fun getString(resId:Int):String{
        return try {
            context.getString(resId)
        }
        catch (e:Exception){
            "Undefined"
        }
    }
}