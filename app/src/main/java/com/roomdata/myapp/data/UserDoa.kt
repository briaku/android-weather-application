package com.roomdata.myapp.data
import androidx.room.*

class UserDoa {
    @Dao
    interface MyDao {
        @Query("SELECT * FROM weatherData")
        fun getAllObjects(): List<User>

        @Insert
        fun insertObject(myObject:User)
        @Delete
        fun delete(user: User)
        @Update
        fun updateUsers(vararg users: User)
    }
}