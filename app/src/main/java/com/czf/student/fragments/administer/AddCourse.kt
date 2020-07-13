package com.czf.student.fragments.administer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.czf.student.R
import com.czf.student.beans.Course
import com.czf.student.dialogs.DatePickerDialog
import com.czf.student.dialogs.PopUpNews
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import com.thecode.aestheticdialogs.AestheticDialog
import kotlinx.android.synthetic.main.fragment_add_course.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddCourse: Fragment() {
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_course,null)
    }

    private val simpleDateFormat: SimpleDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }

    override fun onStart() {
        super.onStart()
        startTime.setOnClickListener {
            DatePickerDialog(activity!!,simpleDateFormat.parse(startTime.text.toString())?: Date(),object :
                DatePickerDialog.DatePickerListener{
                override fun dateChange(dateString: String) {
                    startTime.text = dateString
                }
            }).show()
        }

        endTime.setOnClickListener {
            DatePickerDialog(activity!!,simpleDateFormat.parse(endTime.text.toString())?: Date(),object :
                DatePickerDialog.DatePickerListener{
                override fun dateChange(dateString: String) {
                    endTime.text = dateString
                }
            }).show()
        }

        confirm.setOnClickListener {
            var ready=true
            ready=ready&&courseName.text.isNotEmpty()
            if(!ready){
                AestheticDialog.showEmotion(activity,StringResourceGetter.getString(R.string.error),StringResourceGetter.getString(R.string.course_name),AestheticDialog.ERROR)
                return@setOnClickListener
            }

            val value=try {
                courseCredit.text.toString().toDouble()
            }
            catch (e:Exception){
                ready=false
                0.0
            }
            if(!ready){
                AestheticDialog.showEmotion(activity,StringResourceGetter.getString(R.string.error),StringResourceGetter.getString(R.string.type_error),AestheticDialog.ERROR)
                return@setOnClickListener
            }

            val ids=try {
                courseTeacher.text.split(",")
            }
            catch (e:Exception){
                emptyList<String>()
            }

            val teacherId=ArrayList<Int>()
            for(i in ids) {
                try {
                    teacherId.add(i.toInt())
                }
                catch (e:Exception){
                    AestheticDialog.showEmotion(activity,StringResourceGetter.getString(R.string.error),StringResourceGetter.getString(R.string.type_error),AestheticDialog.ERROR)
                }
            }

            val course = Course(0,courseName.text.toString(),teacherId,value,courseGrade.text.split(","),java.sql.Date(simpleDateFormat.parse(startTime.text.toString())?.time?:System.currentTimeMillis()),java.sql.Date(simpleDateFormat.parse(endTime.text.toString())?.time?:System.currentTimeMillis()), emptyList())
            lifecycleScope.launch {
                val result=NetWork.addCourse(course)
                if(result)
                    PopUpNews.showGoodNews(activity!!,StringResourceGetter.getString(R.string.success))
                else
                    PopUpNews.showBadNews(activity!!,StringResourceGetter.getString(R.string.fail))
            }
        }
    }
}