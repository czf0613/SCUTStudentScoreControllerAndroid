package com.czf.student.fragments.student

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.czf.student.R
import com.czf.student.beans.CourseScore
import com.czf.student.helper.LocalPreferences
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import kotlinx.android.synthetic.main.fragment_student_score.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Score:Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_student_score,null)
    }

    private var scoreList = ArrayList<CourseScore>()

    override fun onStart() {
        super.onStart()
        refresh()

        searchBar.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                GlobalScope.launch(Dispatchers.Main) {
                    val tempList=ArrayList<CourseScore>()
                    for(i in scoreList)
                        if(i.course.toString().contains(s?:"")||i.courseName.contains(s?:""))
                            tempList.add(i)
                    scoreListView.adapter=ScoreAdapter(tempList)
                }
            }
        })

        pullRefresh.setOnRefreshListener {
            refresh()
            pullRefresh.setRefreshing(false)
        }
    }

    inner class ScoreAdapter(scores:ArrayList<CourseScore>?):BaseAdapter() {
        private val scores=scores?: ArrayList()

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            if(scores.isEmpty()){
                val textView= TextView(activity)
                textView.setText(R.string.none)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20.0f)
                textView.gravity= Gravity.CENTER
                return textView
            }

            val view=View.inflate(activity,R.layout.element_student_score,null)
            val score=scores[position]

            val courseId:TextView=view.findViewById(R.id.courseID)
            val courseName:TextView=view.findViewById(R.id.courseName)
            val scoreText:TextView=view.findViewById(R.id.score)
            courseId.text=score.course.toString()
            courseName.text=score.courseName
            scoreText.text= String.format("%.2f",score.score)

            return view
        }

        override fun getItem(position: Int): Any {
            return scores[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return scores.size.coerceAtLeast(1)
        }
    }

    private fun refresh(){
        searchBar.text.clear()

        GlobalScope.launch(Dispatchers.Main) {
            scoreList= ArrayList(NetWork.getScore(LocalPreferences.getInt("id")?:1))
            scoreListView.adapter=ScoreAdapter(scoreList)
        }
    }
}