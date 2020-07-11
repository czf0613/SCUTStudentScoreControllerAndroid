package com.czf.student.dialogs

import android.app.Activity
import com.czf.student.R
import com.czf.student.helper.StringResourceGetter
import com.thecode.aestheticdialogs.AestheticDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PopUpNews {
    suspend fun showGoodNews(activity: Activity,message:String){
        withContext(Dispatchers.Main){
            AestheticDialog.showEmotion(activity, StringResourceGetter.getString(R.string.success),message,AestheticDialog.SUCCESS)
        }
    }

    suspend fun showBadNews(activity: Activity,message:String){
        withContext(Dispatchers.Main){
            AestheticDialog.showEmotion(activity, StringResourceGetter.getString(R.string.fail),message,AestheticDialog.ERROR)
        }
    }
}