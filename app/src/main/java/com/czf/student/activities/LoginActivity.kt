package com.czf.student.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.czf.student.R
import com.czf.student.dialogs.Register
import com.czf.student.helper.LocalPreferences
import com.czf.student.helper.NetWork
import com.czf.student.helper.StringResourceGetter
import com.thecode.aestheticdialogs.AestheticDialog
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userNameEditText.setText(LocalPreferences.getString("userName")?:"")
        userNameEditText.setSelection(userNameEditText.text.length)

        passwordEditText.setText(LocalPreferences.getString("password")?:"")
        when(LocalPreferences.getInt("type")?:0){
            0 -> student.isChecked=true
            1 -> teacher.isChecked=true
            2 -> administer.isChecked=true
        }
    }

    override fun onStart() {
        super.onStart()

        forgetPassword.setOnClickListener {
            SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText(R.string.forget_password)
                .setConfirmButton(R.string.confirm) { it.dismiss() }
                .setContentText(StringResourceGetter.getString(R.string.forget_password_notice))
                .show()
        }

        register.setOnClickListener {
            Register(this).show()
        }

        setIP.setOnClickListener {
            val editText=EditText(this)
            editText.maxLines=1
            editText.hint= StringResourceGetter.getString(R.string.url_hint)
            editText.setText(LocalPreferences.getString("ip")?:"http://")
            editText.setSelection(editText.text.length)

            AlertDialog.Builder(this)
                .setTitle(R.string.set_ip)
                .setView(editText)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel,null)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    val pattern= Pattern.compile("((http|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})")
                    val matcher=pattern.matcher(editText.text.toString())
                    if(matcher.matches()) {
                        LocalPreferences.put("ip", editText.text.toString())
                        AestheticDialog.showEmotion(this,StringResourceGetter.getString(
                            R.string.success
                        ),StringResourceGetter.getString(R.string.match_url),AestheticDialog.SUCCESS)
                    }
                    else
                        AestheticDialog.showEmotion(this,StringResourceGetter.getString(
                            R.string.fail
                        ),StringResourceGetter.getString(R.string.mismatch_url),AestheticDialog.ERROR)
                }
                .show()
        }

        login.setOnClickListener {
            val type:String=when(userSelector.checkedRadioButtonId){
                R.id.student -> "student"
                R.id.teacher -> "teacher"
                R.id.administer -> "administer"
                else -> "student"
            }

            GlobalScope.launch(Dispatchers.Main){
                when(NetWork.login(type,userNameEditText.text.toString(),passwordEditText.text.toString())){
                    200->{
                        LocalPreferences.put("userName",userNameEditText.text.toString())
                        LocalPreferences.put("password",passwordEditText.text.toString())
                        val intent=when(type){
                            "student" -> Intent(this@LoginActivity, StudentActivity::class.java)
                            "teacher" -> Intent(this@LoginActivity, TeacherActivity::class.java)
                            "administer" -> Intent(this@LoginActivity, AdministerActivity::class.java)
                            else -> Intent(this@LoginActivity, StudentActivity::class.java)
                        }
                        startActivity(intent)
                        this@LoginActivity.finish()
                    }
                    404->{
                        SweetAlertDialog(this@LoginActivity,SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(R.string.no_user)
                            .setConfirmButton(R.string.confirm) { it.dismiss() }
                            .setContentText(StringResourceGetter.getString(R.string.no_user))
                            .show()
                    }
                    401->{
                        SweetAlertDialog(this@LoginActivity,SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(R.string.wrong_password)
                            .setConfirmButton(R.string.confirm) { it.dismiss() }
                            .setContentText(StringResourceGetter.getString(R.string.wrong_password))
                            .show()
                    }
                    else->{
                        SweetAlertDialog(this@LoginActivity,SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(R.string.invalid_user)
                            .setConfirmButton(R.string.confirm) { it.dismiss() }
                            .setContentText(StringResourceGetter.getString(R.string.invalid_user))
                            .show()
                    }
                }
            }
        }
    }
}