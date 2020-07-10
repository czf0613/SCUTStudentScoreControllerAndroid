package com.czf.student.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.czf.student.R
import kotlinx.android.synthetic.main.dialog_date_picker.*
import java.util.*

class DatePickerDialog(context: Context, private val date: Date?,private val listener:DatePickerListener):Dialog(context) {
    interface DatePickerListener {
        fun dateChange(dateString: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_date_picker)
        val calendarInstance=Calendar.getInstance()
        calendarInstance.time=date?: Date(System.currentTimeMillis())
        calendar.maxDate=System.currentTimeMillis()
        calendar.init(calendarInstance.get(Calendar.YEAR),calendarInstance.get(Calendar.MONTH),calendarInstance.get(Calendar.DAY_OF_MONTH)
        ) { _, year, monthOfYear, dayOfMonth ->
            listener.dateChange(String.format("%04d-%02d-%02d",year,monthOfYear+1,dayOfMonth))
        }
    }
}