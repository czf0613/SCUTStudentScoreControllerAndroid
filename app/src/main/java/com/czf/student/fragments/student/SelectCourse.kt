package com.czf.student.fragments.student

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.czf.student.R
import com.czf.student.beans.Course
import com.czf.student.dialogs.PopUpNews
import com.czf.student.helper.LocalPreferences
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import kotlinx.android.synthetic.main.fragment_student_select_course.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SelectCourse: Fragment() {
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_student_select_course,null)
    }

    val simpleDateFormat: SimpleDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }

    override fun onStart() {
        super.onStart()
        refresh()

        firstPullRefresh.setOnRefreshListener {
            refresh()
            firstPullRefresh.setRefreshing(false)
        }
        secondPullRefresh.setOnRefreshListener {
            refresh()
            secondPullRefresh.setRefreshing(false)
        }
    }

    inner class CourseInfoAdapter(courses: ArrayList<Course>?, private val status:Int) : BaseAdapter() {
        var courses:ArrayList<Course> = courses?: ArrayList()

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            if(courses.isEmpty()){
                val textView= TextView(activity)
                textView.setText(R.string.none)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20.0f)
                textView.gravity= Gravity.CENTER
                return textView
            }

            val view=View.inflate(activity,R.layout.element_course_info,null)
            val course=courses[position]

            val coursePic: ImageView =view.findViewById(R.id.coursePic)
            if(status==1) {
                Glide.with(activity!!).load(R.mipmap.plus).into(coursePic)
                coursePic.setOnClickListener {
                    lifecycleScope.launch {
                        val result=NetWork.selectCourse(LocalPreferences.getInt("id")?:1, listOf(course.id))
                        if(result) {
                            PopUpNews.showGoodNews(activity!!, StringResourceGetter.getString(R.string.select_course_success))
                            delay(300)
                            refresh()
                        }
                        else
                            PopUpNews.showBadNews(activity!!,StringResourceGetter.getString(R.string.select_course_fail))
                    }
                }
            }
            else {
                Glide.with(activity!!).load(R.mipmap.minus).into(coursePic)
                coursePic.setOnClickListener {
                    lifecycleScope.launch {
                        val result=NetWork.removeCourse(LocalPreferences.getInt("id")?:1, course.id)
                        if(result) {
                            PopUpNews.showGoodNews(activity!!,StringResourceGetter.getString(R.string.remove_course_success))
                            delay(300)
                            refresh()
                        }
                        else
                            PopUpNews.showBadNews(activity!!,StringResourceGetter.getString(R.string.remove_course_fail))
                    }
                }
            }

            val courseID: TextView =view.findViewById(R.id.courseID)
            courseID.text=course.id.toString()

            val courseName: TextView =view.findViewById(R.id.courseName)
            courseName.text=course.courseName

            val courseTeacher: TextView =view.findViewById(R.id.courseTeacher)
            lifecycleScope.launch { courseTeacher.text=
                NetWork.getTeacherNamesWithId(course.teachers?: emptyList()) }

            val courseCredit: TextView =view.findViewById(R.id.courseCredit)
            courseCredit.text=String.format("%.1f",course.credit)

            val courseStartDate: TextView =view.findViewById(R.id.courseStartDate)
            courseStartDate.text=simpleDateFormat.format(course.startDate)

            val courseEndDate: TextView =view.findViewById(R.id.courseEndDate)
            courseEndDate.text=simpleDateFormat.format(course.endDate)
            return view
        }

        override fun getItem(position: Int): Any {
            return courses[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return courses.size.coerceAtLeast(1)
        }
    }

    private fun refresh(){
        lifecycleScope.launch {
            val selectedCourse= ArrayList<Course>(NetWork.getCourses(LocalPreferences.getInt("id")?:1,"selected"))
            selectedCourses.adapter=CourseInfoAdapter(selectedCourse,0)
        }
        lifecycleScope.launch {
            val chooseAbleCourses= ArrayList<Course>(NetWork.getCourses(LocalPreferences.getInt("id")?:1,"selectable"))
            coursesToSelect.adapter=CourseInfoAdapter(chooseAbleCourses,1)
        }
    }
}