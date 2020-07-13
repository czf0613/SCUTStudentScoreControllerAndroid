package com.czf.student.fragments.teacher

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.czf.student.R
import com.czf.student.beans.CourseScore
import com.czf.student.helper.LocalPreferences
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import kotlinx.android.synthetic.main.fragment_student_score.*
import kotlinx.coroutines.launch

class AverageScore: Fragment() {
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_student_score,null)
    }

    lateinit var scoreAdapter: ScoreAdapter
    private val scoreList by lazy { ArrayList<CourseScore>() }

    override fun onStart() {
        super.onStart()
        refresh()

        pullRefresh.setOnRefreshListener {
            refresh()
            pullRefresh.setRefreshing(false)
        }

        searchBar.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lifecycleScope.launch {
                    val tempList=ArrayList<CourseScore>()
                    for(i in scoreList)
                        if(i.course.toString().contains(s.toString())||i.courseName.contains(s.toString()))
                            tempList.add(i)
                    scoreAdapter=ScoreAdapter(tempList)
                    scoreListView.adapter=scoreAdapter
                }
            }
        })
    }

    private fun refresh(){
        lifecycleScope.launch {
            scoreList.clear()
            scoreList.addAll(NetWork.getAvgScore(LocalPreferences.getInt("id")?:1))
            scoreAdapter=ScoreAdapter(scoreList)
            scoreListView.adapter=scoreAdapter
        }
    }

    inner class ScoreAdapter(scores:ArrayList<CourseScore>?): BaseAdapter() {
        private val scores=scores?: ArrayList()

        @SuppressLint("ViewHolder", "SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            if(position==0){
                val textView= TextView(activity)
                textView.text= StringResourceGetter.getString(R.string.my_teaching_avg)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20.0f)
                textView.gravity= Gravity.CENTER
                return textView
            }

            if(scores.isEmpty()){
                val textView= TextView(activity)
                textView.setText(R.string.none)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20.0f)
                textView.gravity= Gravity.CENTER
                return textView
            }

            val view=View.inflate(activity,R.layout.element_student_score,null)
            val score=scores[position-1]

            val courseId: TextView =view.findViewById(R.id.courseID)
            val courseName: TextView =view.findViewById(R.id.courseName)
            val scoreText: TextView =view.findViewById(R.id.score)
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
            return scores.size.coerceAtLeast(1)+1
        }
    }
}