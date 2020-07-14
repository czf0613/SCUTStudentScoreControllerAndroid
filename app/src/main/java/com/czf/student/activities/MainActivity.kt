package com.czf.student.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.czf.student.R
import com.czf.student.helper.LocalPreferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        progressBar.progress=0

        val countDownTimer=object :CountDownTimer(3000,30){
            override fun onFinish() {
                if(LocalPreferences.getString("ip")==null)
                    LocalPreferences.put("ip","http://106.54.9.186:23333")
                val intent=Intent(this@MainActivity,
                    LoginActivity::class.java)
                startActivity(intent)
                this@MainActivity.finish()
            }

            override fun onTick(millisUntilFinished: Long) {
                progressBar.progress=(100-millisUntilFinished/30).toInt()
            }
        }
        countDownTimer.start()
    }
}