package com.czf.student

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
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
                val intent=Intent(this@MainActivity,LoginActivity::class.java)
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