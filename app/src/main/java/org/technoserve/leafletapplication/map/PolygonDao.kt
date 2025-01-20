package org.technoserve.leafletapplication.map

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "plot_table")
data class PlotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "area")
    val area: Double,

    @ColumnInfo(name = "centroid_lat")
    val centroidLat: Double,

    @ColumnInfo(name = "centroid_lng")
    val centroidLng: Double,

    @ColumnInfo(name = "points")
    val points: String, // JSON-serialized points data

    @ColumnInfo(name = "accuracy")
    val accuracy: Float,

    @ColumnInfo(name = "farmer_name")
    val farmerName: String,

    @ColumnInfo(name = "plot_address")
    val plotAddress: String
)


@Dao
interface PlotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlot(plot: PlotEntity)

    @Query("SELECT * FROM plot_table")
    fun getAllPlots(): LiveData<List<PlotEntity>>

}