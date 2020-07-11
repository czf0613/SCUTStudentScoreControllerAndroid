package com.czf.student.helper

import com.alibaba.fastjson.JSON
import com.czf.student.R
import com.czf.student.beans.Course
import com.czf.student.beans.CourseScore
import com.czf.student.beans.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

object NetWork {
    private val shortClient=OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    suspend fun login(type:String,userName:String,password:String):Int {
        return withContext(Dispatchers.IO){
            val formBody=FormBody.Builder()
                .add("userName", userName)
                .add("password", SHA.passwordEncode(password))
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/$type/login")
                .post(formBody)
                .build()

            try {
                val response: Response = shortClient.newCall(request).execute()
                val code=response.code
                if(code==200)
                    LocalPreferences.put("id", response.body?.string()?.toInt()?:0)
                code
            }
            catch (e:Exception){
                404
            }
        }
    }

    suspend fun register(type:String,userName:String,password:String,content:Any):Int {
        return withContext(Dispatchers.IO){
            val formBody=FormBody.Builder()
                .add("userName", userName)
                .add("password", SHA.passwordEncode(password))
                .add("content",JSON.toJSONString(content))
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/$type/register")
                .post(formBody)
                .build()

            try {
                val response: Response = shortClient.newCall(request).execute()
                response.code
            }
            catch (e:Exception){
                500
            }
        }
    }

    suspend fun getStudentInfo(id:Int):Student?{
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/student/$id/self")
                .get()
                .build()

            try {
                val response= shortClient.newCall(request).execute()
                if(response.code==200)
                    JSON.parseObject(response.body?.string(),Student::class.java)
                else
                    null
            }
            catch (e:Exception){
                null
            }
        }
    }

    suspend fun getCourses(id:Int,type:String="all"):List<Course>{
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/student/$id/courses/$type")
                .get()
                .build()

            try{
                val response= shortClient.newCall(request).execute()
                if(response.code==200)
                    JSON.parseArray(response.body?.string(),Course::class.java)
                else
                    emptyList()
            }
            catch (e:Exception){
                emptyList<Course>()
            }
        }
    }

    suspend fun getTeacherNamesWithId(id:List<Int>):String {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("${LocalPreferences.getString("ip")}/teacher/getNameWithIDs?IDList=${JSON.toJSONString(id)}")
                .get()
                .build()

            try {
                val response = shortClient.newCall(request).execute()
                if (response.code == 200)
                    response.body!!.string()!!
                else
                    StringResourceGetter.getString(R.string.none)
            }
            catch (e: Exception) {
                StringResourceGetter.getString(R.string.none)
            }
        }
    }

    suspend fun selectCourse(id:Int,courses:List<Int>):Boolean {
        return withContext(Dispatchers.IO){
            val formBody=FormBody.Builder()
                .add("courseId",JSON.toJSONString(courses))
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/student/$id/choose")
                .post(formBody)
                .build()

            var response= shortClient.newCall(request).execute()

            response.code==200
        }
    }

    suspend fun removeCourse(id:Int,course:Int):Boolean {
        return withContext(Dispatchers.IO){
            val formBody=FormBody.Builder()
                .add("courseId",course.toString())
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/student/$id/remove")
                .post(formBody)
                .build()

            val response= shortClient.newCall(request).execute()

            response.code==200
        }
    }

    suspend fun getScore(id:Int):List<CourseScore>{
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/student/$id/score/all")
                .get()
                .build()

            val response= shortClient.newCall(request).execute()

            if(response.code==200)
                JSON.parseArray(response.body!!.string(),CourseScore::class.java)
            else
                emptyList()
        }
    }
}