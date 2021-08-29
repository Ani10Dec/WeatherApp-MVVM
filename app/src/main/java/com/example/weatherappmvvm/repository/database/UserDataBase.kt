package com.example.weatherappmvvm.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 3)
abstract class UserDataBase : RoomDatabase() {
    abstract val getUserDao: UserDao

    companion object {
        @Volatile  // To visible throughout the application
        private var INSTANCE: UserDataBase? = null
        fun getDatabaseInstance(context: Context): UserDataBase {
            synchronized(this) {   //TO initiate only once
                var instances = INSTANCE
                if (instances == null) {
                    instances = Room.databaseBuilder(
                        context.applicationContext,
                        UserDataBase::class.java,
                        "User_db"
                    ).build()
                }
                return instances
            }
        }
    }
}