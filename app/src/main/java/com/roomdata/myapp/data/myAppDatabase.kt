package com.roomdata.myapp.data
import android.content.Context
import androidx.room.*

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class MyAppDatabase: RoomDatabase() {
    abstract fun myDao(): UserDoa.MyDao

    companion object {
        private var instance: MyAppDatabase? = null

        fun getInstance(context: Context): MyAppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyAppDatabase::class.java,
                    "my_database"
                ).build()
            }
            return instance!!
        }
    }


    @Dao
    interface MyDao {
        @Query("SELECT * FROM weatherData")
        fun getAllObjects(): List<User>

        @Insert
        fun insertObject(myObject: User)

        @Delete
        fun delete(user: User)

        @Update
        fun updateUsers(vararg users: User)

        @Query("SELECT * FROM weatherData WHERE id = :userId")
        fun getUserById(userId: Int): User
    }
   /* @Entity(tableName="weatherData", indices = [Index(value = ["id", "temp", "feeltemp", "description","windspeed" ], unique = true)])
    data class User (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name= "id") val id: Int,
        @ColumnInfo(name= "temp") val temperature: String,
        @ColumnInfo(name= "feeltemp") val feeltemp: String,
        @ColumnInfo(name= "description") val description: String,
        @ColumnInfo(name= "windspeed") val windspeed: String
    )
*/
}