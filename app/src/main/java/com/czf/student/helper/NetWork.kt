package com.czf.student.helper

import com.alibaba.fastjson.JSON
import com.czf.student.R
import com.czf.student.beans.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

object NetWork {
    private val shortClient by lazy { OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build() }

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
                if(code==200) {
                    LocalPreferences.put("id", response.body?.string()?.toInt()?:0)
                    when(type) {
                        "student" -> LocalPreferences.put("type", 0)
                        "teacher" -> LocalPreferences.put("type", 1)
                        "administer" -> LocalPreferences.put("type", 2)
                    }
                }
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

    suspend fun resetPassword(userId:Int):Boolean {
        return withContext(Dispatchers.IO){
            val formBody=FormBody.Builder()
                .add("userId", userId.toString())
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/administer/resetPassword")
                .post(formBody)
                .build()

            try {
                val response: Response = shortClient.newCall(request).execute()
                response.code==200
            }
            catch (e:Exception){
                false
            }
        }
    }

    suspend fun modifyPassword(userName:String,password: String,newPassword:String):String {
        return withContext(Dispatchers.IO){
            val formBody=FormBody.Builder()
                .add("userName", userName)
                .add("password", SHA.passwordEncode(password))
                .add("newPassword",SHA.passwordEncode(newPassword))
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/modify")
                .post(formBody)
                .build()

            try {
                val response: Response = shortClient.newCall(request).execute()
                response.body?.string()?:StringResourceGetter.getString(R.string.unknown_error)
            }
            catch (e:Exception){
                StringResourceGetter.getString(R.string.unknown_error)
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

    suspend fun getTeacherInfo(id:Int):Teacher?{
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/teacher/$id/self")
                .get()
                .build()

            try {
                val response= shortClient.newCall(request).execute()
                if(response.code==200)
                    JSON.parseObject(response.body?.string(),Teacher::class.java)
                else
                    null
            }
            catch (e:Exception){
                null
            }
        }
    }

    suspend fun getCourseInfo(id:Int):Course?{
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/administer/getInfo/course/$id")
                .get()
                .build()

            try {
                val response= shortClient.newCall(request).execute()
                if(response.code==200)
                    JSON.parseObject(response.body?.string(),Course::class.java)
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

    suspend fun getTeacherCourses(id:Int):List<Course>{
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/administer/teacherCourse/$id")
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
                    response.body!!.string()
                else
                    StringResourceGetter.getString(R.string.none)
            }
            catch (e: Exception) {
                StringResourceGetter.getString(R.string.none)
            }
        }
    }

    suspend fun getStudentNameWithId(id:Int):String {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("${LocalPreferences.getString("ip")}/student/$id/name")
                .get()
                .build()

            try {
                val response = shortClient.newCall(request).execute()
                if (response.code == 200)
                    response.body!!.string()
                else
                    StringResourceGetter.getString(R.string.unknown)
            }
            catch (e: Exception) {
                StringResourceGetter.getString(R.string.unknown)
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

            try{
                val response= shortClient.newCall(request).execute()
                response.code==200
            }
            catch (e:Exception){
                false
            }
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

            try{
                val response= shortClient.newCall(request).execute()
                response.code==200
            }
            catch (e:Exception){
                false
            }
        }
    }

    suspend fun getScore(id:Int):List<CourseScore>{
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/student/$id/score/all")
                .get()
                .build()

            try {
                val response= shortClient.newCall(request).execute()

                if(response.code==200)
                    JSON.parseArray(response.body!!.string(),CourseScore::class.java)
                else
                    emptyList()
            }
            catch (e:Exception){
                emptyList<CourseScore>()
            }
        }
    }

    suspend fun getCourseById(id:Int):String {
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/administer/getCourse/$id/name")
                .get()
                .build()

            try {
                val response= shortClient.newCall(request).execute()

                if(response.code==200)
                    response.body!!.string()
                else
                    StringResourceGetter.getString(R.string.unknown_error)
            }
            catch (e:Exception){
                StringResourceGetter.getString(R.string.unknown_error)
            }
        }
    }

    suspend fun getSingleStudentScore(id:Int,stuId:Int):List<CourseScore>{
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/teacher/$id/studentScore/$stuId")
                .get()
                .build()

            try {
                val response= shortClient.newCall(request).execute()

                if(response.code==200)
                    JSON.parseArray(response.body!!.string(),CourseScore::class.java)
                else
                    emptyList()
            }
            catch (e:Exception){
                emptyList<CourseScore>()
            }
        }
    }

    suspend fun getAvgScore(id:Int):List<CourseScore>{
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/teacher/$id/avg")
                .get()
                .build()

            try {
                val response= shortClient.newCall(request).execute()

                if(response.code==200)
                    JSON.parseArray(response.body!!.string(),CourseScore::class.java)
                else
                    emptyList()
            }
            catch (e:Exception){
                emptyList<CourseScore>()
            }
        }
    }

    suspend fun getOverallAvgScore():List<CourseScore>{
        return withContext(Dispatchers.IO){
            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/administer/avg/all")
                .get()
                .build()

            try {
                val response= shortClient.newCall(request).execute()

                if(response.code==200)
                    JSON.parseArray(response.body!!.string(),CourseScore::class.java)
                else
                    emptyList()
            }
            catch (e:Exception){
                emptyList<CourseScore>()
            }
        }
    }

    suspend fun modifyScore(stuId:Int,courseId:Int,score:Double):Boolean {
        return withContext(Dispatchers.IO){
            val scoreObject=Score(stuId, courseId, score)

            val formBody=FormBody.Builder()
                .add("content",JSON.toJSONString(scoreObject))
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/teacher/modifyScore")
                .post(formBody)
                .build()

            try{
                val response= shortClient.newCall(request).execute()
                response.code==200
            }
            catch (e:Exception){
                false
            }
        }
    }

    suspend fun superModify(type:String,content:Any):Boolean{
        return withContext(Dispatchers.IO){
            val formBody=FormBody.Builder()
                .add("content",JSON.toJSONString(content))
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/administer/modify/$type")
                .post(formBody)
                .build()

            try{
                val response= shortClient.newCall(request).execute()
                response.code==200
            }
            catch (e:Exception){
                false
            }
        }
    }

    suspend fun addCourse(content:Course):Boolean{
        return withContext(Dispatchers.IO){
            val formBody=FormBody.Builder()
                .add("content",JSON.toJSONString(content))
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/administer/addCourse")
                .post(formBody)
                .build()

            try{
                val response= shortClient.newCall(request).execute()
                response.code==200
            }
            catch (e:Exception){
                false
            }
        }
    }

    suspend fun submitScore(content:List<Score>):String?{
        return withContext(Dispatchers.IO){
            val formBody=FormBody.Builder()
                .add("teacherId", LocalPreferences.getInt("id").toString())
                .add("content",JSON.toJSONString(content))
                .build()

            val request=Request.Builder()
                .url("${LocalPreferences.getString("ip")}/teacher/addScore")
                .post(formBody)
                .build()

            try {
                val response= shortClient.newCall(request).execute()

                if(response.code==200)
                    null
                else
                    response.body?.string()?:StringResourceGetter.getString(R.string.unknown_error)
            }
            catch (e:Exception){
                StringResourceGetter.getString(R.string.unknown_error)
            }
        }
    }
}