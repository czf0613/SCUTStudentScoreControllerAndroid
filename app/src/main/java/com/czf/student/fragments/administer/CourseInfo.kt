package com.czf.student.fragments.administer

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.czf.student.R
import com.czf.student.beans.Course
import com.czf.student.dialogs.DatePickerDialog
import com.czf.student.dialogs.PopUpNews
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import com.thecode.aestheticdialogs.AestheticDialog
import kotlinx.android.synthetic.main.element_course_info.*
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CourseInfo(private val courseId:Int): Fragment() {
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.element_course_info,null)
    }

    lateinit var course: Course
    private val simpleDateFormat: SimpleDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }

    override fun onStart() {
        super.onStart()
        Glide.with(this).load(R.mipmap.person).into(coursePic)

        lifecycleScope.launch {
            course=NetWork.getCourseInfo(courseId)?: Course(0,"", emptyList(),0.0, emptyList(), Date(System.currentTimeMillis()), Date(System.currentTimeMillis()), emptyList())
            courseID.text=course.id.toString()
            courseName.text=course.courseName
            lifecycleScope.launch { courseTeacher.text=NetWork.getTeacherNamesWithId(course.teachers?: emptyList()) }
            courseCredit.text=String.format("%.1f",course.credit)
            courseStartDate.text=simpleDateFormat.format(course.startDate)
            courseEndDate.text=simpleDateFormat.format(course.endDate)
        }

        courseName.setOnClickListener {
            val editText= EditText(activity)
            editText.setHint(R.string.course_name)
            editText.maxLines=1
            editText.setText(courseName.text)
            editText.setSelection(editText.text.length)

            AlertDialog.Builder(activity!!)
                .setTitle(R.string.course_name)
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    courseName.text=editText.text
                    course.courseName=editText.text.toString()
                }
                .show()
        }

        courseCredit.setOnClickListener {
            val editText= EditText(activity)
            editText.setHint(R.string.course_credit)
            editText.inputType=InputType.TYPE_NUMBER_FLAG_DECIMAL
            editText.maxLines=1
            editText.setText(courseCredit.text)
            editText.setSelection(editText.text.length)

            AlertDialog.Builder(activity!!)
                .setTitle(R.string.course_credit)
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    val value=try{
                        editText.text.toString().toDouble()
                    }
                    catch (e:Exception){
                        AestheticDialog.showEmotion(activity, StringResourceGetter.getString(R.string.error), StringResourceGetter.getString(R.string.type_error), AestheticDialog.WARNING)
                        course.credit
                    }
                    courseCredit.text=value.toString()
                    course.credit=value
                }
                .show()
        }

        courseTeacher.setOnClickListener {
            val editText= EditText(activity)
            editText.setHint(R.string.course_teachers)
            editText.maxLines=1
            val text= when {
                course.teachers?.isEmpty() != false -> {
                    StringResourceGetter.getString(R.string.none)
                }
                course.teachers!!.size==1 -> {
                    course.teachers!![0].toString()
                }
                else -> {
                    var temp=course.teachers!![0].toString()
                    for(i in 1 until course.teachers!!.size)
                        temp+=",${course.teachers!![i]}"
                    temp
                }
            }
            editText.setText(text)
            editText.setSelection(editText.text.length)

            AlertDialog.Builder(activity!!)
                .setTitle(R.string.course_teachers)
                .setView(editText)
                .setMessage("请注意格式，用半角逗号隔开")
                .setCancelable(true)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    val teachers=editText.text.toString().split(",")
                    val ids=ArrayList<Int>()
                    for(i in teachers){
                        try {
                            val value=i.toInt()
                            ids.add(value)
                        }
                        catch (e:Exception){
                            lifecycleScope.launch {
                                PopUpNews.showBadNews(activity!!,StringResourceGetter.getString(R.string.type_error))
                            }
                        }
                    }
                    course.teachers=ids
                    lifecycleScope.launch { courseTeacher.text=NetWork.getTeacherNamesWithId(course.teachers?: emptyList()) }
                }
                .show()
        }

        courseStartDate.setOnClickListener {
            DatePickerDialog(activity!!,simpleDateFormat.parse(courseStartDate.text.toString())?: Date(),object :
                DatePickerDialog.DatePickerListener{
                override fun dateChange(dateString: String) {
                    courseStartDate.text = dateString
                    course.startDate=Date(simpleDateFormat.parse(dateString)?.time?:System.currentTimeMillis())
                }
            }).show()
        }

        courseEndDate.setOnClickListener {
            DatePickerDialog(activity!!,simpleDateFormat.parse(courseEndDate.text.toString())?: Date(),object :
                DatePickerDialog.DatePickerListener{
                override fun dateChange(dateString: String) {
                    courseEndDate.text = dateString
                    course.endDate=Date(simpleDateFormat.parse(dateString)?.time?:System.currentTimeMillis())
                }
            }).show()
        }
    }
}