package com.czf.student.fragments.administer

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.czf.student.R
import com.czf.student.beans.Course
import com.czf.student.dialogs.PopUpNews
import com.czf.student.helper.LocalPreferences
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import kotlinx.android.synthetic.main.fragment_overview_select_course.*
import kotlinx.android.synthetic.main.fragment_student_select_course.coursesToSelect
import kotlinx.android.synthetic.main.fragment_student_select_course.selectedCourses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SelectCourse:Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_overview_select_course,null)
    }

    val simpleDateFormat: SimpleDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }

    override fun onStart() {
        super.onStart()
        searchBar.inputType= InputType.TYPE_CLASS_NUMBER

        searchButton.setOnClickListener {
            val value=try {
                searchBar.text.toString().toInt()
            }
            catch (e:Exception){
                0
            }

            val type=when(individualType.checkedRadioButtonId){
                R.id.student -> 0
                R.id.teacher -> 1
                else -> 0
            }

            if(type==0)
                refresh(value)
            else{
                coursesToSelect.adapter=null
                GlobalScope.launch(Dispatchers.Main){
                    val selectedCourse= ArrayList<Course>(NetWork.getTeacherCourses(value))
                    selectedCourses.adapter=CourseInfoAdapter(selectedCourse)
                }
            }
        }

        firstPullRefresh.setOnRefreshListener {
            searchButton.performClick()
            firstPullRefresh.setRefreshing(false)
        }

        secondPullRefresh.setOnRefreshListener {
            searchButton.performClick()
            secondPullRefresh.setRefreshing(false)
        }
    }

    inner class SelectCourseAdapter(courses: ArrayList<Course>?, private val status:Int,private val stuId:Int) : BaseAdapter() {
        private var courses:ArrayList<Course> = courses?: ArrayList()

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
                    GlobalScope.launch(Dispatchers.Main) {
                        val result= NetWork.selectCourse(stuId, listOf(course.id))
                        if(result) {
                            PopUpNews.showGoodNews(activity!!, StringResourceGetter.getString(R.string.select_course_success))
                            delay(300)
                            refresh(stuId)
                        }
                        else
                            PopUpNews.showBadNews(activity!!, StringResourceGetter.getString(R.string.select_course_fail))
                    }
                }
            }
            else {
                Glide.with(activity!!).load(R.mipmap.minus).into(coursePic)
                coursePic.setOnClickListener {
                    GlobalScope.launch(Dispatchers.Main) {
                        val result= NetWork.removeCourse(stuId, course.id)
                        if(result) {
                            PopUpNews.showGoodNews(activity!!, StringResourceGetter.getString(R.string.remove_course_success))
                            delay(300)
                            refresh(stuId)
                        }
                        else
                            PopUpNews.showBadNews(activity!!, StringResourceGetter.getString(R.string.remove_course_fail))
                    }
                }
            }

            val courseID: TextView =view.findViewById(R.id.courseID)
            courseID.text=course.id.toString()

            val courseName: TextView =view.findViewById(R.id.courseName)
            courseName.text=course.courseName

            val courseTeacher: TextView =view.findViewById(R.id.courseTeacher)
            GlobalScope.launch(Dispatchers.Main) { courseTeacher.text=
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

    inner class CourseInfoAdapter(courses: ArrayList<Course>?) : BaseAdapter() {
        var courses:ArrayList<Course> = courses?: ArrayList()

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            if(courses.isEmpty()){
                val textView=TextView(activity)
                textView.setText(R.string.none)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20.0f)
                textView.gravity=Gravity.CENTER
                return textView
            }

            val view=View.inflate(activity,R.layout.element_course_info,null)

            val coursePic:ImageView=view.findViewById(R.id.coursePic)
            Glide.with(this@SelectCourse).load(R.mipmap.person).into(coursePic)

            val course=courses[position]

            val courseID:TextView=view.findViewById(R.id.courseID)
            courseID.text=course.id.toString()

            val courseName:TextView=view.findViewById(R.id.courseName)
            courseName.text=course.courseName

            val courseTeacher:TextView=view.findViewById(R.id.courseTeacher)
            GlobalScope.launch(Dispatchers.Main) { courseTeacher.text=NetWork.getTeacherNamesWithId(course.teachers?: emptyList()) }

            val courseCredit:TextView=view.findViewById(R.id.courseCredit)
            courseCredit.text=String.format("%.1f",course.credit)

            val courseStartDate:TextView=view.findViewById(R.id.courseStartDate)
            courseStartDate.text=simpleDateFormat.format(course.startDate)

            val courseEndDate:TextView=view.findViewById(R.id.courseEndDate)
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

    private fun refresh(id:Int){
        GlobalScope.launch(Dispatchers.Main) {
            val selectedCourse= ArrayList<Course>(NetWork.getCourses(id,"selected"))
            selectedCourses.adapter=SelectCourseAdapter(selectedCourse,0,id)
        }
        GlobalScope.launch(Dispatchers.Main) {
            val chooseAbleCourses= ArrayList<Course>(NetWork.getCourses(id,"selectable"))
            coursesToSelect.adapter=SelectCourseAdapter(chooseAbleCourses,1,id)
        }
    }
}