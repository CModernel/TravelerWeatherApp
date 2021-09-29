package com.example.travelerweatherapp.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Weather::class, User::class],
    version=5
)

abstract class AppDb : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
    abstract fun userDao(): UserDao

    companion object{
        @Volatile
        private var INSTANCE: AppDb? = null

        fun getDatabase(context: Context): AppDb{
            val tempInstance = INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "travelerDB"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}