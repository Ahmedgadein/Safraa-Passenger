package com.dinder.rihla.rider.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dinder.rihla.rider.common.Constants.DATABASE_NAME
import com.dinder.rihla.rider.data.local.UserDao
import com.dinder.rihla.rider.data.model.User

@Database(entities = [User::class], version = 1)
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
                .build()
        }
    }
}
