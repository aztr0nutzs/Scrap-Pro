package com.example.data.local

import androidx.room.TypeConverter
import com.example.domain.model.MetalGrade

class Converters {
    @TypeConverter
    fun fromMetalGrade(value: MetalGrade): String = value.name

    @TypeConverter
    fun toMetalGrade(value: String): MetalGrade = enumValueOf<MetalGrade>(value)
}
