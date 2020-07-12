package com.czf.student.fragments.administer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.czf.student.R
import com.czf.student.dialogs.PopUpNews
import com.czf.student.helper.LocalPreferences
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import kotlinx.android.synthetic.main.fragment_edit_information.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditInformation:Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_information,null)
    }

    lateinit var studentInfo: StudentInfo
    lateinit var teacherInfo: TeacherInfo
    lateinit var courseInfo: CourseInfo
    var ableToClick=false

    override fun onStart() {
        super.onStart()
        searchButton.setOnClickListener {
            val type=typeSelector.selectedItemPosition
            val id=try {
                searchBar.text.toString().toInt()
            }
            catch (e:Exception){
                LocalPreferences.getInt("id")?:1
            }

            val transaction=fragmentManager!!.beginTransaction()
            when(type){
                0 -> {
                    studentInfo=StudentInfo(id)
                    transaction.replace(R.id.editInterface,studentInfo)
                }
                1 -> {
                    teacherInfo=TeacherInfo(id)
                    transaction.replace(R.id.editInterface,teacherInfo)
                }
                2 -> {
                    courseInfo=CourseInfo(id)
                    transaction.replace(R.id.editInterface,courseInfo)
                }
            }
            ableToClick=true
            transaction.commit()
        }

        confirmModify.setOnClickListener {
            if(!ableToClick)
                return@setOnClickListener

            when(typeSelector.selectedItemPosition){
                0 -> {
                    GlobalScope.launch {
                        val result=NetWork.superModify("student",studentInfo.student)
                        if(result)
                            PopUpNews.showGoodNews(activity!!,StringResourceGetter.getString(R.string.modify_success))
                        else
                            PopUpNews.showBadNews(activity!!,StringResourceGetter.getString(R.string.modify_fail))
                    }
                }
                1 -> {
                    GlobalScope.launch {
                        val result=NetWork.superModify("teacher",teacherInfo.teacher)
                        if(result)
                            PopUpNews.showGoodNews(activity!!,StringResourceGetter.getString(R.string.modify_success))
                        else
                            PopUpNews.showBadNews(activity!!,StringResourceGetter.getString(R.string.modify_fail))
                    }
                }
                2 -> {
                    GlobalScope.launch {
                        val result=NetWork.superModify("course",courseInfo.course)
                        if(result)
                            PopUpNews.showGoodNews(activity!!,StringResourceGetter.getString(R.string.modify_success))
                        else
                            PopUpNews.showBadNews(activity!!,StringResourceGetter.getString(R.string.modify_fail))
                    }
                }
            }
        }
    }
}