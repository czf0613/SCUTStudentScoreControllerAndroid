package com.czf.student.beans

import java.sql.Date

data class Course(val id:Int,val courseName:String,val teachers:List<Int>?,val credit:Double,val grade:List<String>,val startDate:Date,val endDate: Date,val students:List<Int>?)