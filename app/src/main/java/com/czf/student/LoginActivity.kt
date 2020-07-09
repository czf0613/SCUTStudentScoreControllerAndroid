package com.czf.student

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
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
    var type:String="student"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
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

        }

        setIP.setOnClickListener {
            val editText=EditText(this)
            editText.maxLines=1
            editText.hint= StringResourceGetter.getString(R.string.url_hint)
            editText.setText(LocalPreferences.getString("ip")?:"")

            AlertDialog.Builder(this)
                .setTitle(R.string.set_ip)
                .setView(editText)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel) { _,_ -> }
                .setPositiveButton(R.string.confirm) {_,_ ->
                    val pattern= Pattern.compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})")
                    val matcher=pattern.matcher(editText.text.toString())
                    if(matcher.matches()) {
                        AestheticDialog.showEmotion(this,StringResourceGetter.getString(R.string.success),StringResourceGetter.getString(R.string.match_url),AestheticDialog.SUCCESS)
                        LocalPreferences.put("ip", editText.text.toString())
                    }
                    else
                        AestheticDialog.showEmotion(this,StringResourceGetter.getString(R.string.fail),StringResourceGetter.getString(R.string.mismatch_url),AestheticDialog.ERROR)
                }
                .show()
        }

        login.setOnClickListener {
            when(userSelector.checkedRadioButtonId){
                R.id.student -> type="student"
                R.id.teacher -> type="teacher"
                R.id.administer -> type="administer"
            }

            GlobalScope.launch(Dispatchers.Main){
                when(NetWork.login(type,userNameEditText.text.toString(),passwordEditText.text.toString())){
                    200->{
                        val intent=Intent(this@LoginActivity,StudentActivity::class.java)
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