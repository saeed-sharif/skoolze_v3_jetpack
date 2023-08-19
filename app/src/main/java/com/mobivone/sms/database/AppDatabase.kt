package com.mobivone.sms.database

import android.content.Context
import androidx.room.*
import com.mobivone.sms.entities.databaseModel
import kotlinx.coroutines.flow.Flow

@Database(entities = [databaseModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        private const val DATABASE_NAME = "Messages_Database"
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                        .build()
                }
                return instance!!
            }
        }
    }

    @Dao
    abstract class MessageDao {
        @Insert
        abstract fun insert(databaseModel: databaseModel)

        @Update
        abstract suspend fun update(databaseModel: databaseModel)

        @Delete
        abstract suspend fun delete(databaseModel: databaseModel)

        @Query("DELETE FROM MessageTable")
        abstract suspend fun deleteAllMessages()

        @Query("SELECT * FROM MessageTable")
        abstract fun getAllMessages(): Flow<List<databaseModel>>

        @Query("Delete FROM MessageTable WHERE Message=:messageContent")
        abstract suspend fun deleteSendMessage(messageContent: String)

        @Query("DELETE FROM MessageTable WHERE id = :messageId")
        abstract suspend fun deleteMessageById(messageId: Long)


        @Query("SELECT COUNT(*) FROM MessageTable")
        abstract  fun getCount(): Flow<Int>
    }
}