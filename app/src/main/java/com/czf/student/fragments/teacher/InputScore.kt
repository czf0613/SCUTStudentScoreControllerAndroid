package com.czf.student.fragments.teacher

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.czf.student.R
import com.czf.student.beans.CourseScore
import com.czf.student.beans.Score
import com.czf.student.dialogs.PopUpNews
import com.czf.student.helper.LocalPreferences
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import com.thecode.aestheticdialogs.AestheticDialog
import kotlinx.android.synthetic.main.fragment_input_student_score.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InputScore: Fragment() {
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_input_student_score,null)
    }

    private lateinit var scoreAdapter: ScoreAdapter
    private var refreshJob:Job?=null

    override fun onStart() {
        super.onStart()

        scoreAdapter=ScoreAdapter(ArrayList())
        scoreListView.adapter=scoreAdapter

        studentIdEditText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                refreshJob?.cancel()
                refreshJob=lifecycleScope.launch {
                    delay(300)
                    val id=try {
                        studentIdEditText.text.toString().toInt()
                    }
                    catch (e:Exception){
                        0
                    }

                    launch { studentName.text="${NetWork.getStudentNameWithId(id)}学生的成绩：" }
                    launch {
                        scoreAdapter=ScoreAdapter(ArrayList(NetWork.getSingleStudentScore(LocalPreferences.getInt("id")?:1,id)))
                        scoreListView.adapter=scoreAdapter
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        write.setOnClickListener {
            val arrayList=ArrayList<Score>()
            val stuId=try {
                studentIdEditText.text.toString().toInt()
            }
            catch (e:Exception){
                AestheticDialog.showEmotion(activity!!,StringResourceGetter.getString(R.string.error),StringResourceGetter.getString(R.string.type_error),AestheticDialog.ERROR)
                return@setOnClickListener
            }
            for(i in scoreAdapter.scores)
                arrayList.add(Score(stuId,i.course,i.score))
            lifecycleScope.launch {
                val result=NetWork.submitScore(arrayList)
                if(result==null)
                    PopUpNews.showGoodNews(activity!!,StringResourceGetter.getString(R.string.success))
                else
                    PopUpNews.showBadNews(activity!!,result)
            }
        }
    }

    inner class ScoreAdapter(scores:ArrayList<CourseScore>?): BaseAdapter() {
        val scores=scores?:ArrayList()

        @SuppressLint("ViewHolder", "SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            if(position==scores.size){
                val imageView=ImageView(activity)
                imageView.layoutParams=LinearLayout.LayoutParams(150,150)
                Glide.with(this@InputScore).load(R.mipmap.plus).into(imageView)
                if(studentName.text.toString()!="未知"){
                    imageView.setOnClickListener {
                        val editText=EditText(activity)
                        editText.setHint(R.string.course_id)
                        AlertDialog.Builder(activity!!)
                            .setTitle(R.string.course_id)
                            .setView(editText)
                            .setCancelable(true)
                            .setNegativeButton(R.string.cancel,null)
                            .setPositiveButton(R.string.confirm) { _, _ ->
                                val courseId=try {
                                    editText.text.toString().toInt()
                                }
                                catch (e:Exception){
                                    0
                                }
                                if(scores.any { it.course==courseId||courseId==0 })
                                    AestheticDialog.showEmotion(activity!!,StringResourceGetter.getString(R.string.error),StringResourceGetter.getString(R.string.course_repeat),AestheticDialog.ERROR)
                                else {
                                    scores.add(CourseScore(courseId,0.0))
                                    scoreAdapter=ScoreAdapter(scoreAdapter.scores)
                                    scoreListView.adapter=scoreAdapter
                                }
                            }
                            .show()
                    }
                }
                else{
                    imageView.setOnClickListener {
                        AestheticDialog.showEmotion(activity!!,StringResourceGetter.getString(R.string.error),StringResourceGetter.getString(R.string.user_id_hint),AestheticDialog.ERROR)
                    }
                }
                return imageView
            }

            val view=View.inflate(activity,R.layout.element_student_score,null)
            val score=scores[position]

            val courseId: TextView =view.findViewById(R.id.courseID)
            val courseName: TextView =view.findViewById(R.id.courseName)
            val scoreText: TextView =view.findViewById(R.id.score)
            courseId.text=score.course.toString()
            if(score.courseName=="未知")
                lifecycleScope.launch { courseName.text=NetWork.getCourseById(score.course) }
            else
                courseName.text=score.courseName
            scoreText.text= String.format("%.2f",score.score)

            scoreText.setOnClickListener {
                val editText= EditText(activity)
                editText.maxLines=1
                editText.inputType= InputType.TYPE_NUMBER_FLAG_DECIMAL
                editText.hint= StringResourceGetter.getString(R.string.score_hint)
                editText.setText(scoreText.text)
                editText.setSelection(editText.text.length)

                AlertDialog.Builder(activity!!)
                    .setTitle(R.string.score_hint)
                    .setView(editText)
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel,null)
                    .setPositiveButton(R.string.confirm) { _, _ ->
                        val value=try {
                            editText.text.toString().toDouble()
                        }
                        catch (e:Exception){
                            AestheticDialog.showEmotion(activity,StringResourceGetter.getString(R.string.error),StringResourceGetter.getString(R.string.type_error),AestheticDialog.WARNING)
                            score.score
                        }
                        score.score=value
                        scoreText.text=String.format("%.2f",value)
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
            return scores.size+1
        }
    }
}