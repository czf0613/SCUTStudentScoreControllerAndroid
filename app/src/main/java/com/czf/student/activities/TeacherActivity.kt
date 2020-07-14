package com.czf.student.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.czf.student.R
import com.czf.student.fragments.teacher.AverageScore
import com.czf.student.fragments.teacher.EditScore
import com.czf.student.fragments.teacher.SelfInfo
import com.czf.student.fragments.teacher.InputScore
import kotlinx.android.synthetic.main.activity_teacher.*
import nl.joery.animatedbottombar.AnimatedBottomBar

class TeacherActivity : AppCompatActivity() {
    private val selfInfo by lazy { SelfInfo() }
    private val singleScore by lazy { InputScore() }
    private val averageScore by lazy { AverageScore() }
    private val editScore by lazy { EditScore() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragment,selfInfo)
        transaction.commit()
    }

    override fun onStart() {
        super.onStart()
        fragmentSelector.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener{
            override fun onTabSelected(lastIndex: Int, lastTab: AnimatedBottomBar.Tab?, newIndex: Int, newTab: AnimatedBottomBar.Tab) {
                val transaction=supportFragmentManager.beginTransaction()
                when(newIndex){
                    0 -> transaction.replace(R.id.mainFragment,selfInfo)
                    1 -> transaction.replace(R.id.mainFragment,singleScore)
                    2 -> transaction.replace(R.id.mainFragment,averageScore)
                    3 -> transaction.replace(R.id.mainFragment,editScore)
                }
                transaction.commit()
            }
        })
    }
}