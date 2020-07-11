package com.czf.student.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.czf.student.R
import com.czf.student.fragments.student.CourseInfo
import com.czf.student.fragments.student.Score
import com.czf.student.fragments.student.SelectCourse
import com.czf.student.fragments.student.SelfInfo
import kotlinx.android.synthetic.main.activity_student.*
import nl.joery.animatedbottombar.AnimatedBottomBar

class StudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragment,selfInfo)
        transaction.commit()
    }

    val selfInfo:SelfInfo by lazy { SelfInfo() }
    val courseInfo: CourseInfo by lazy { CourseInfo() }
    val selectCourse: SelectCourse by lazy { SelectCourse() }
    val score: Score by lazy { Score() }

    override fun onStart() {
        super.onStart()

        fragmentSelector.setOnTabSelectListener(object :AnimatedBottomBar.OnTabSelectListener{
            override fun onTabSelected(lastIndex: Int, lastTab: AnimatedBottomBar.Tab?, newIndex: Int, newTab: AnimatedBottomBar.Tab) {
                val transaction=supportFragmentManager.beginTransaction()
                when(newIndex){
                    0 -> transaction.replace(R.id.mainFragment,selfInfo)
                    1 -> transaction.replace(R.id.mainFragment,courseInfo)
                    2 -> transaction.replace(R.id.mainFragment,selectCourse)
                    3 -> transaction.replace(R.id.mainFragment,score)
                }
                transaction.commit()
            }
        })
    }
}