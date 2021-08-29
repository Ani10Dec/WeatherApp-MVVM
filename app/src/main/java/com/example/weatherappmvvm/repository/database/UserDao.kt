package com.example.weatherappmvvm.repository.database

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity)

    @Delete
    suspend fun deleteUser(userEntity: UserEntity)

    @Query(value = "SELECT * FROM userTable WHERE number = :number")
    suspend fun getUser(number: String): UserEntity?
}