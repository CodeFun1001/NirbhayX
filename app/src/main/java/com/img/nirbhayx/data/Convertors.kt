package com.img.nirbhayx.data

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromSafetyCategory(category: SafetyCategory): String {
        return category.name
    }

    @TypeConverter
    fun toSafetyCategory(value: String): SafetyCategory {
        return SafetyCategory.valueOf(value)
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        return json?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
