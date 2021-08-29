package com.example.weatherappmvvm.repository.database

import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserTable")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var number: String,
    var name: String,
    var gender: String,
    var dob: String,
    var age: String,
    var address1: String,
    var address2: String?,
    var pinCode: String,
    var district: String,
    var state: String
)
