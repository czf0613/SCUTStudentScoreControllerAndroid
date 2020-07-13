package com.czf.student.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.czf.student.R
import com.czf.student.fragments.administer.AddCourse
import com.czf.student.fragments.administer.EditInformation
import com.czf.student.fragments.administer.Score
import com.czf.student.fragments.administer.SelectCourse
import kotlinx.android.synthetic.main.activity_administer.*
import nl.joery.animatedbottombar.AnimatedBottomBar

class AdministerActivity : AppCompatActivity() {
    private val editInformation by lazy { EditInformation() }
    private val score by lazy { Score() }
    private val selectCourse by lazy { SelectCourse() }
    private val addCourse by lazy { AddCourse() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administer)

        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragment,editInformation)
        transaction.commit()
    }

    override fun onStart() {
        super.onStart()
        fragmentSelector.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener{
            override fun onTabSelected(lastIndex: Int, lastTab: AnimatedBottomBar.Tab?, newIndex: Int, newTab: AnimatedBottomBar.Tab) {
                val transaction=supportFragmentManager.beginTransaction()
                when(newIndex){
                    0 -> transaction.replace(R.id.mainFragment,editInformation)
                    1 -> transaction.replace(R.id.mainFragment,score)
                    2 -> transaction.replace(R.id.mainFragment,selectCourse)
                    3 -> transaction.replace(R.id.mainFragment,addCourse)
                }
                transaction.commit()
            }
        })
    }
}