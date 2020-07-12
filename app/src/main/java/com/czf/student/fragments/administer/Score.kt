package com.czf.student.fragments.administer

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.czf.student.R
import com.czf.student.beans.CourseScore
import com.czf.student.dialogs.PopUpNews
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import com.thecode.aestheticdialogs.AestheticDialog
import kotlinx.android.synthetic.main.fragment_overview_score.*
import kotlinx.android.synthetic.main.fragment_overview_score.scoreListView
import kotlinx.android.synthetic.main.fragment_overview_score.searchBar
import kotlinx.android.synthetic.main.fragment_overview_score.searchButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Score:Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_overview_score,null)
    }

    private var checkType = 0
    lateinit var singleScoreAdapter: SingleScoreAdapter
    lateinit var avgScoreAdapter: AvgScoreAdapter

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        searchBar.inputType=InputType.TYPE_CLASS_NUMBER
        searchBar.setHint(R.string.teacher_search_hint)

        scoreType.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.individualScore -> {
                    searchBar.setHint(R.string.teacher_search_hint)
                    checkType=0
                }
                R.id.groupScore -> {
                    searchBar.setHint(R.string.enter_course_id)
                    checkType=1
                }
            }
        }

        searchButton.setOnClickListener {
            val value=try {
                searchBar.text.toString().toInt()
            }
            catch (e:Exception){
                0
            }
            when(checkType){
                0 -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        singleScoreAdapter=SingleScoreAdapter(ArrayList(NetWork.getScore(value)))
                        scoreListView.adapter=singleScoreAdapter
                    }
                }
                1 -> {
                    GlobalScope.launch(Dispatchers.Main){
                        val result=ArrayList(NetWork.getOverallAvgScore())
                        result.removeIf { it.course!=value&&value!=0 }
                        avgScoreAdapter=AvgScoreAdapter(result)
                        scoreListView.adapter=avgScoreAdapter
                    }
                }
            }
        }

        pullRefresh.setOnRefreshListener {
            searchButton.performClick()
            pullRefresh.setRefreshing(false)
        }
    }

    inner class SingleScoreAdapter(scores:ArrayList<CourseScore>?): BaseAdapter() {
        private val scores=scores?: ArrayList()

        @SuppressLint("ViewHolder", "SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            if(position==0){
                val textView= TextView(activity)
                textView.text= StringResourceGetter.getString(R.string.unknown)
                GlobalScope.launch(Dispatchers.Main) {
                    val stuId=try {
                        searchBar.text.toString().toInt()
                    }
                    catch (e:Exception){
                        0
                    }
                    textView.text="${NetWork.getStudentInfo(stuId)?.name?:"未知用户"}的成绩："
                }
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

            scoreText.setOnClickListener {
                val editText= EditText(activity)
                editText.maxLines=1
                editText.inputType=InputType.TYPE_NUMBER_FLAG_DECIMAL
                editText.hint= StringResourceGetter.getString(R.string.score_hint)
                editText.setText(scoreText.text)
                editText.setSelection(editText.text.length)

                AlertDialog.Builder(activity!!)
                    .setTitle(R.string.score_hint)
                    .setView(editText)
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel,null)
                    .setPositiveButton(R.string.confirm) { _, _ ->
                        val stuId=try {
                            searchBar.text.toString().toInt()
                        }
                        catch (e:Exception){
                            0
                        }
                        val value=try {
                            editText.text.toString().toDouble()
                        }
                        catch (e:Exception){
                            AestheticDialog.showEmotion(activity,
                                StringResourceGetter.getString(R.string.error),
                                StringResourceGetter.getString(R.string.type_error),
                                AestheticDialog.WARNING)
                            score.score
                        }
                        GlobalScope.launch(Dispatchers.Main) {
                            val result= NetWork.modifyScore(stuId,score.course,value)
                            if(result){
                                PopUpNews.showGoodNews(activity!!, StringResourceGetter.getString(R.string.modify_success))
                                delay(300)
                                searchButton.performClick()
                            }
                            else
                                PopUpNews.showBadNews(activity!!, StringResourceGetter.getString(R.string.modify_fail))
                        }
                    }
                    .show()
            }

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

    inner class AvgScoreAdapter(scores:ArrayList<CourseScore>?): BaseAdapter() {
        private val scores=scores?: ArrayList()

        @SuppressLint("ViewHolder", "SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            if(position==0){
                val textView= TextView(activity)
                textView.text= StringResourceGetter.getString(R.string.average_score)
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