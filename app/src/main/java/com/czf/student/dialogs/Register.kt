package com.czf.student.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import com.czf.student.R
import com.czf.student.beans.Administer
import com.czf.student.beans.Student
import com.czf.student.beans.Teacher
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import com.thecode.aestheticdialogs.AestheticDialog
import kotlinx.android.synthetic.main.dialog_register.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class Register(context: Context) : Dialog(context) {
    private val simpleDateFormat=SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_register)
    }

    private fun scanForActivity(cont:Context?):Activity? {
        return when (cont) {
            is Activity -> cont
            is ContextWrapper -> scanForActivity(cont.baseContext)
            else -> null
        }
    }

    override fun onStart() {
        super.onStart()
        birthday.setOnClickListener {
            DatePickerDialog(context,simpleDateFormat.parse(birthday.text.toString()),object :DatePickerDialog.DatePickerListener{
                override fun dateChange(dateString: String) {
                    birthday.text = dateString
                }
            }).show()
        }

        enrollmentTime.setOnClickListener {
            DatePickerDialog(context,simpleDateFormat.parse(enrollmentTime.text.toString()),object :DatePickerDialog.DatePickerListener{
                override fun dateChange(dateString: String) {
                    enrollmentTime.text = dateString
                }
            }).show()
        }

        confirm.setOnClickListener {
            var ready=true
            ready=ready&&nameEditText.text.isNotEmpty()
            if(!ready) {
                AestheticDialog.showEmotion(
                    scanForActivity(context),
                    StringResourceGetter.getString(R.string.error),
                    StringResourceGetter.getString(R.string.name_hint),
                    AestheticDialog.ERROR
                )
                return@setOnClickListener
            }

            ready=ready&&majorEditText.text.isNotEmpty()
            if(!ready) {
                AestheticDialog.showEmotion(
                    scanForActivity(context),
                    StringResourceGetter.getString(R.string.error),
                    StringResourceGetter.getString(R.string.major_hint),
                    AestheticDialog.ERROR
                )
                return@setOnClickListener
            }

            ready=ready&&(passwordEditText.text.toString() == passwordEditTextAgain.text.toString())
            if(!ready) {
                AestheticDialog.showEmotion(
                    scanForActivity(context),
                    StringResourceGetter.getString(R.string.error),
                    StringResourceGetter.getString(R.string.password_double_check_fail),
                    AestheticDialog.ERROR
                )
                return@setOnClickListener
            }

            val gender = when(genderRadioGroup.checkedRadioButtonId) {
                R.id.male -> 1
                else -> 2
            }

            val type:String=when(userSelector.checkedRadioButtonId){
                R.id.student -> "student"
                R.id.teacher -> "teacher"
                R.id.administer -> "administer"
                else -> "student"
            }

            GlobalScope.launch(Dispatchers.Main) {
                val result=when(type){
                    "student"->{
                        val student=Student(nameEditText.text.toString(),gender,gradeEditText.text.toString(),majorEditText.text.toString(),classroomEditText.text.toString(),Date(simpleDateFormat.parse(birthday.text.toString())!!.time),Date(simpleDateFormat.parse(enrollmentTime.text.toString())!!.time))
                        NetWork.register(type,userNameEditText.text.toString(),passwordEditText.text.toString(),student)
                    }
                    "teacher"->{
                        val teacher=Teacher(nameEditText.text.toString(),gender,gradeEditText.text.toString(),majorEditText.text.toString())
                        NetWork.register(type,userNameEditText.text.toString(),passwordEditText.text.toString(),teacher)
                    }
                    "administer"->{
                        val administer=Administer(10)
                        NetWork.register(type,userNameEditText.text.toString(),passwordEditText.text.toString(),administer)
                    }
                    else -> 500
                }

                when(result){
                    400 -> AestheticDialog.showEmotion(
                        scanForActivity(context),
                        StringResourceGetter.getString(R.string.error),
                        StringResourceGetter.getString(R.string.user_name_exist),
                        AestheticDialog.ERROR
                    )
                    200 -> {
                        AestheticDialog.showEmotion(
                            scanForActivity(context),
                            StringResourceGetter.getString(R.string.success),
                            StringResourceGetter.getString(R.string.register_success),
                            AestheticDialog.SUCCESS
                        )
                        dismiss()
                    }
                    else -> AestheticDialog.showEmotion(
                        scanForActivity(context),
                        StringResourceGetter.getString(R.string.error),
                        StringResourceGetter.getString(R.string.unknown_error),
                        AestheticDialog.ERROR
                    )
                }
            }
        }
    }
}