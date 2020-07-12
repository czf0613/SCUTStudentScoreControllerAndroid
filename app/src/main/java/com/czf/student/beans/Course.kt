package com.czf.student.beans

import java.sql.Date

data class Course(val id:Int, var courseName:String, var teachers:List<Int>?, var credit:Double, val grade:List<String>, var startDate:Date, var endDate: Date, val students:List<Int>?)