package org.technoserve.leafletapplication.map


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//@Database(entities = [PlotEntity::class], version = 2, exportSchema = false)
//@TypeConverters(Converters::class)
//abstract class PlotDatabase : RoomDatabase() {
//    abstract fun plotDao(): PlotDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: PlotDatabase? = null
//
//        fun getDatabase(context: Context): PlotDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    PlotDatabase::class.java,
//                    "plot_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}

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
                    "plot_database_v2"
                ).fallbackToDestructiveMigration()
                    //.addMigrations(MIGRATION_1_2) // Add migration strategy
                .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration strategy for version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new columns to the existing table
                db.execSQL("ALTER TABLE plot_table ADD COLUMN farmer_name TEXT DEFAULT ''")
                db.execSQL("ALTER TABLE plot_table ADD COLUMN plot_address TEXT DEFAULT ''")
            }
        }
    }
}
