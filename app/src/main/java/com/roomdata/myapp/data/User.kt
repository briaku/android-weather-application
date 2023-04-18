package com.roomdata.myapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName="weatherData", indices = [Index(value = ["id", "temp", "feeltemp", "description","windspeed" ], unique = true)])
data class User (
    @PrimaryKey
    @ColumnInfo(name= "id") val id: Int,
    @ColumnInfo(name= "temp") val temperature: String,
    @ColumnInfo(name= "feeltemp") val feeltemp: String,
    @ColumnInfo(name= "description") val description: String,
    @ColumnInfo(name= "windspeed") val windspeed: String
)