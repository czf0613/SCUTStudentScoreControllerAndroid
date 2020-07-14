package com.czf.student.helper

import java.security.MessageDigest
import java.util.*

object SHA {
    private fun toHex(byteArray: ByteArray): String {
        return with(StringBuilder()) {
            byteArray.forEach {
                val hex = it.toInt() and (0xFF)
                val hexStr = Integer.toHexString(hex)
                if (hexStr.length == 1) {
                    append("0").append(hexStr)
                } else {
                    append(hexStr)
                }
            }
            toString()
        }
    }

    private fun sha256(str:String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val result = digest.digest(str.toByteArray())
        return toHex(result)
    }

    fun passwordEncode(rawPassword:String):String{
        return sha256("${sha256(rawPassword.toUpperCase(Locale.ROOT))}${rawPassword.toLowerCase(Locale.ROOT)}").toUpperCase(Locale.ROOT)
    }
}