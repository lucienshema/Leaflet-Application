package org.technoserve.leafletapplication.map


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PlotEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PlotDatabase : RoomDatabase() {
    abstract fun plotDao(): PlotDao

    companion object {
        @Volatile
        private var INSTANCE: PlotDatabase? = null

        fun getDatabase(context: Context): PlotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlotDatabase::class.java,
                    "plot_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}