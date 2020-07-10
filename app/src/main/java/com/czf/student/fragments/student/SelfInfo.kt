package com.czf.student.fragments.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.czf.student.R
import kotlinx.android.synthetic.main.fragment_self_information.*

class SelfInfo:Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_self_information,null)
    }

    override fun onStart() {
        super.onStart()
        icon.setImageResource(R.mipmap.person)
    }
}