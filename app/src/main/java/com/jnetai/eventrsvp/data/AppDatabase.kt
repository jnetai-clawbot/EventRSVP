package com.jnetai.eventrsvp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jnetai.eventrsvp.data.dao.EventDao
import com.jnetai.eventrsvp.data.dao.GuestDao
import com.jnetai.eventrsvp.data.entity.Event
import com.jnetai.eventrsvp.data.entity.Guest

@Database(entities = [Event::class, Guest::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun guestDao(): GuestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "eventrsvp.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}