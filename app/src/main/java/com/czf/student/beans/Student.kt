package com.czf.student.beans

import java.sql.Date

data class Student(val id:Int,
                   var name:String, val gender:Int, var grade:String, var major:String, var clazz:String, val birthday:Date, val enrollmentTime:Date)