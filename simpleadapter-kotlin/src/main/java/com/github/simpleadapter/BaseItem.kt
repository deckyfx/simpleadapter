package com.itp.android.simpleadapter_kotlin

import android.content.Context
import android.view.MotionEvent
import android.view.View

import com.google.gson.Gson

import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class BaseItem {
    @Throws(IOException::class)
    protected fun <T : BaseItem> parseGson(text: String): T {
        return Gson().fromJson(text, this.javaClass) as T
    }

    protected fun toJsonString(): String {
        return Gson().toJson(this)
    }

    companion object {
        @Throws(IOException::class)
        fun <T : BaseItem> ParseGson(text: String, klas: Class<out BaseItem>): T {
            return Gson().fromJson<BaseItem>(text, klas) as T
        }

        fun ToJsonString(obj: Any): String {
            return Gson().toJson(obj)
        }

        val gSon: Gson
            get() = Gson()

        @JvmOverloads fun parseDateAsString(format: String, date: Date, locale: Locale = Locale.getDefault()): String {
            val df = SimpleDateFormat(format, locale)
            return df.format(date)
        }

        @JvmOverloads fun parseStringAsDate(format: String, dateString: String, locale: Locale = Locale.getDefault()): Date {
            val df = SimpleDateFormat(format, locale)
            try {
                return df.parse(dateString)
            } catch (e: ParseException) {
                e.printStackTrace()
                return Date()
            }

        }
    }
}
