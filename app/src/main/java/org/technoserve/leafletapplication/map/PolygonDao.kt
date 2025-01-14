package org.technoserve.leafletapplication.map

import androidx.room.*

@Entity(tableName = "plot_table")
data class PlotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "area")
    val area: Double,

    @ColumnInfo(name = "centroid_lat")
    val centroidLat: Double,

    @ColumnInfo(name = "centroid_lng")
    val centroidLng: Double,

    @ColumnInfo(name = "points")
    val points: String, // JSON-serialized points data

    @ColumnInfo(name = "accuracy")
    val accuracy: Float
)
@Dao
interface PlotDao {
    @Insert
    suspend fun insertPlot(plotEntity: PlotEntity): Long

    // Add other methods like update, delete, etc. as needed
}