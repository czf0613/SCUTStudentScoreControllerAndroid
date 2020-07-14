package com.czf.student.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import com.czf.student.R
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import com.thecode.aestheticdialogs.AestheticDialog
import kotlinx.android.synthetic.main.dialog_modify_password.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ModifyPassword(context: Context):Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_modify_password)
    }

    private fun scanForActivity(cont:Context?): Activity? {
        return when (cont) {
            is Activity -> cont
            is ContextWrapper -> scanForActivity(cont.baseContext)
            else -> null
        }
    }

    override fun onStart() {
        super.onStart()
        confirm.setOnClickListener {
            if(userNameEditText.text.isEmpty()||passwordEditText.text.isEmpty()||passwordEditTextAgain.text.isEmpty()){
                AestheticDialog.showEmotion(scanForActivity(context)!!,StringResourceGetter.getString(R.string.error),StringResourceGetter.getString(R.string.type_error),AestheticDialog.ERROR)
                return@setOnClickListener
            }

            GlobalScope.launch(Dispatchers.Main) {
                val result= NetWork.modifyPassword(userNameEditText.text.toString(),passwordEditText.text.toString(),passwordEditTextAgain.text.toString())
                if(result=="修改成功"){
                    PopUpNews.showGoodNews(scanForActivity(context)!!, StringResourceGetter.getString(R.string.modify_success))
                    dismiss()
                }
                else
                    PopUpNews.showBadNews(scanForActivity(context)!!, result)
            }
        }
    }
}