package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "habit_completions", primaryKeys = ["date", "habitId"])
data class HabitCompletion(
    val date: String, // YYYY-MM-DD
    val habitId: String,
    val isCompleted: Boolean
)

@Entity(tableName = "completed_days")
data class CompletedDay(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val wasSuccessful: Boolean
)

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val winsText: String,
    val setbacksText: String
)

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun getCompletionsForDate(date: String): Flow<List<HabitCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("SELECT * FROM completed_days ORDER BY date DESC")
    fun getAllCompletedDays(): Flow<List<CompletedDay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedDay(day: CompletedDay)

    @Query("DELETE FROM completed_days WHERE date = :date")
    suspend fun deleteCompletedDay(date: String)

    @Query("SELECT * FROM journal_entries WHERE date = :date")
    fun getJournalEntryForDate(date: String): Flow<JournalEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry)
}

@Database(entities = [HabitCompletion::class, CompletedDay::class, JournalEntry::class], version = 2, exportSchema = false)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "brotherhood_protocol_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
