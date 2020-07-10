package com.czf.student.beans

import java.sql.Date

data class Student(val name:String,val gender:Int,val grade:String,val major:String,val clazz:String,val birthday:Date,val enrollmentTime:Date)