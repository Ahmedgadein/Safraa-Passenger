package com.dinder.rihla.rider.data.local.db

import androidx.room.TypeConverter
import com.dinder.rihla.rider.data.model.Role

class Converters {
    @TypeConverter
    fun toRole(value: String) = enumValueOf<Role>(value)

    @TypeConverter
    fun fromRole(value: Role) = value.name
}
