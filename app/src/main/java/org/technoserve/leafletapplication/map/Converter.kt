package org.technoserve.leafletapplication.map

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromPointsList(value: List<Point>): String = Gson().toJson(value)

    @TypeConverter
    fun toPointsList(value: String): List<Point> =
        Gson().fromJson(value, Array<Point>::class.java).toList()
}
