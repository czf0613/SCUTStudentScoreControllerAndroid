package com.czf.student.fragments.student

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.czf.student.R
import com.czf.student.helper.LocalPreferences
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import kotlinx.android.synthetic.main.fragment_self_information.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SelfInfo: Fragment() {
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_self_information,null)
    }

    override fun onStart() {
        super.onStart()
        Glide.with(this).load(R.mipmap.person).into(icon)
        lifecycleScope.launch {
            val student=NetWork.getStudentInfo(LocalPreferences.getInt("id")?:1)
            val simpleDateFormat=SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            userId.text=student?.id.toString()
            name.text = student?.name?:StringResourceGetter.getString(R.string.unknown)
            gender.text=when(student?.gender?:1){
                1 -> StringResourceGetter.getString(R.string.male)
                0 -> StringResourceGetter.getString(R.string.female)
                else -> StringResourceGetter.getString(R.string.unknown)
            }
            birthday.text=simpleDateFormat.format(student?.birthday?:System.currentTimeMillis())
            grade.text=student?.grade?:StringResourceGetter.getString(R.string.unknown)
            major.text=student?.major?:StringResourceGetter.getString(R.string.unknown)
            classroom.text=student?.clazz?:StringResourceGetter.getString(R.string.unknown)
            enrollmentTime.text=simpleDateFormat.format(student?.enrollmentTime?:System.currentTimeMillis())
        }
    }
}