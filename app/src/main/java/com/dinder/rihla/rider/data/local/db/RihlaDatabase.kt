package com.dinder.rihla.rider.data.local.db

import android.content.Context
import androidx.room.* // ktlint-disable no-wildcard-imports
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dinder.rihla.rider.common.Constants.DATABASE_NAME
import com.dinder.rihla.rider.data.local.UserDao
import com.dinder.rihla.rider.data.model.User

@Database(
    entities = [User::class],
    version = 3,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
@TypeConverters(Converters::class)
abstract class RihlaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: RihlaDatabase? = null

        fun getInstance(context: Context): RihlaDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): RihlaDatabase {
            return Room.databaseBuilder(context, RihlaDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_2_3)
                .build()
        }
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE user ADD COLUMN points INTEGER NOT NULL DEFAULT 0")
    }
}
