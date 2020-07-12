package com.czf.student.fragments.administer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.czf.student.R
import com.czf.student.beans.Teacher
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import kotlinx.android.synthetic.main.fragment_self_information.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TeacherInfo(private val teacherId:Int):Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_self_information,null)
    }

    lateinit var teacher: Teacher

    override fun onStart() {
        super.onStart()
        Glide.with(this).load(R.mipmap.person).into(icon)
        GlobalScope.launch(Dispatchers.Main) {
            teacher=NetWork.getTeacherInfo(teacherId)?: Teacher(0,"",0,"", emptyList())
            userId.text= teacher.id.toString()
            name.text = teacher.name ?:StringResourceGetter.getString(R.string.unknown)
            gender.text=when(teacher.gender){
                1 -> StringResourceGetter.getString(R.string.male)
                0 -> StringResourceGetter.getString(R.string.female)
                else -> StringResourceGetter.getString(R.string.unknown)
            }
            major.text= teacher.major
        }

        name.setOnClickListener {
            val editText= EditText(activity)
            editText.setHint(R.string.name_hint)
            editText.maxLines=1
            editText.setText(name.text)
            editText.setSelection(editText.text.length)

            AlertDialog.Builder(activity!!)
                .setTitle(R.string.name)
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    name.text=editText.text
                    teacher.name=editText.text.toString()
                }
                .show()
        }
    }
}