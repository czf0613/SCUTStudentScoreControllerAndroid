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
import com.czf.student.beans.Student
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import kotlinx.android.synthetic.main.fragment_self_information.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class StudentInfo(private val stuId:Int):Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_self_information,null)
    }

    lateinit var student: Student

    override fun onStart() {
        super.onStart()
        Glide.with(this).load(R.mipmap.person).into(icon)
        GlobalScope.launch(Dispatchers.Main) {
            student=NetWork.getStudentInfo(stuId)?: Student(0,"",0,"","","", Date(System.currentTimeMillis()), Date(System.currentTimeMillis()))
            val simpleDateFormat=SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            userId.text= student.id.toString()
            name.text = student.name ?:StringResourceGetter.getString(R.string.unknown)
            gender.text=when(student.gender ?:1){
                1 -> StringResourceGetter.getString(R.string.male)
                0 -> StringResourceGetter.getString(R.string.female)
                else -> StringResourceGetter.getString(R.string.unknown)
            }
            birthday.text=simpleDateFormat.format(student.birthday)
            grade.text= student.grade
            major.text= student.major
            classroom.text= student.clazz
            enrollmentTime.text=simpleDateFormat.format(student.enrollmentTime)
        }

        name.setOnClickListener {
            val editText=EditText(activity)
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
                    student.name=editText.text.toString()
                }
                .show()
        }

        grade.setOnClickListener {
            val editText=EditText(activity)
            editText.setHint(R.string.grade_hint)
            editText.maxLines=1
            editText.setText(grade.text)
            editText.setSelection(editText.text.length)

            AlertDialog.Builder(activity!!)
                .setTitle(R.string.grade)
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    grade.text=editText.text
                    student.grade=editText.text.toString()
                }
                .show()
        }

        major.setOnClickListener {
            val editText=EditText(activity)
            editText.setHint(R.string.major_hint)
            editText.maxLines=1
            editText.setText(major.text)
            editText.setSelection(editText.text.length)

            AlertDialog.Builder(activity!!)
                .setTitle(R.string.major)
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    major.text=editText.text
                    student.major=editText.text.toString()
                }
                .show()
        }

        classroom.setOnClickListener {
            val editText=EditText(activity)
            editText.setHint(R.string.classroom_hint)
            editText.maxLines=1
            editText.setText(classroom.text)
            editText.setSelection(editText.text.length)

            AlertDialog.Builder(activity!!)
                .setTitle(R.string.classroom)
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    classroom.text=editText.text
                    student.clazz=editText.text.toString()
                }
                .show()
        }
    }
}