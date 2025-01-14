package org.technoserve.leafletapplication.map

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "polygons")
data class PolygonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val area: Double,
    val centroidLat: Double,
    val centroidLng: Double,
    val accuracy: Float,
    val points: String // JSON string of coordinates
)
data class PlotData(
    val area: Double,
    val centroidLat: Double,
    val centroidLng: Double,
    val points: List<Point>, // Assuming points is a list of custom objects
    val accuracy: Float
)

data class Point(
    val lat: Double,
    val lng: Double
)