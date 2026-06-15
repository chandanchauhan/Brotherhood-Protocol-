package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CompletedDay
import com.example.data.HabitCompletion
import com.example.data.HabitDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val database = HabitDatabase.getDatabase(application)
    private val dao = database.habitDao()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)

    // Current selected date in "yyyy-MM-dd" format
    private val _selectedDate = MutableStateFlow(getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Display formatted selected date (e.g., "Monday, 15 Jun 2026")
    val displayDate: StateFlow<String> = _selectedDate.map { dateStr ->
        if (dateStr == getTodayDateString()) {
            "Today"
        } else {
            val date = sdf.parse(dateStr) ?: Date()
            displayFormat.format(date)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Today")

    // completions for the currently active selected date
    val todayCompletions: StateFlow<Map<String, Boolean>> = _selectedDate
        .flatMapLatest { date ->
            dao.getCompletionsForDate(date).map { list ->
                list.associate { it.habitId to it.isCompleted }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Journal Entry for current date
    val currentJournalEntry: StateFlow<com.example.data.JournalEntry?> = _selectedDate
        .flatMapLatest { date ->
            dao.getJournalEntryForDate(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All registered completed days list
    val completedDays: StateFlow<List<String>> = dao.getAllCompletedDays()
        .map { list -> list.filter { it.wasSuccessful }.map { it.date } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Is currently selected date marked completed as a completed day
    val isTodayCompletedDay: StateFlow<Boolean> = combine(_selectedDate, dao.getAllCompletedDays()) { date, list ->
        list.any { it.date == date && it.wasSuccessful }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Current daily streak calculated dynamically
    val currentStreak: StateFlow<Int> = completedDays.map { completedList ->
        calculateStreak(completedList, getTodayDateString())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // SharedPreferences for Theme (Persistent)
    private val sharedPrefs = application.getSharedPreferences("brotherhood_prefs", Context.MODE_PRIVATE)
    private val _isDarkMode = MutableStateFlow(sharedPrefs.getBoolean("dark_mode", true)) // Default to dark mode matching "soil" HTML theme
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        val newVal = !_isDarkMode.value
        _isDarkMode.value = newVal
        sharedPrefs.edit().putBoolean("dark_mode", newVal).apply()
    }

    fun getTodayDateString(): String {
        return sdf.format(Date())
    }

    fun isFutureDate(daysToAdd: Int): Boolean {
        val date = sdf.parse(_selectedDate.value) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
        
        val todayStr = getTodayDateString()
        val nextStr = sdf.format(calendar.time)
        
        val todayDate = sdf.parse(todayStr)!!
        val nextDate = sdf.parse(nextStr)!!
        
        return nextDate.after(todayDate)
    }

    fun adjustDate(days: Int) {
        val date = sdf.parse(_selectedDate.value) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        
        val todayStr = getTodayDateString()
        val nextStr = sdf.format(calendar.time)
        val todayDate = sdf.parse(todayStr)!!
        val nextDate = sdf.parse(nextStr)!!
        
        if (!nextDate.after(todayDate)) {
            _selectedDate.value = nextStr
        }
    }

    fun toggleHabitCompletion(habitId: String) {
        viewModelScope.launch {
            val date = _selectedDate.value
            val currentMap = todayCompletions.value
            val wasCompleted = currentMap[habitId] ?: false
            val newVal = !wasCompleted
            dao.insertCompletion(HabitCompletion(date = date, habitId = habitId, isCompleted = newVal))
        }
    }

    fun toggleDayCompleted() {
        viewModelScope.launch {
            val date = _selectedDate.value
            val currentVal = isTodayCompletedDay.value
            if (currentVal) {
                dao.deleteCompletedDay(date)
            } else {
                dao.insertCompletedDay(CompletedDay(date = date, wasSuccessful = true))
            }
        }
    }

    fun saveJournalEntry(wins: String, setbacks: String) {
        viewModelScope.launch {
            val date = _selectedDate.value
            dao.insertJournalEntry(com.example.data.JournalEntry(date = date, winsText = wins, setbacksText = setbacks))
        }
    }

    private fun calculateStreak(completedDates: List<String>, todayStr: String): Int {
        if (completedDates.isEmpty()) return 0
        val completedSet = completedDates.toSet()
        val calendar = Calendar.getInstance()
        
        val parsedToday = sdf.parse(todayStr) ?: return 0
        calendar.time = parsedToday
        
        var streak = 0
        val isTodayCompleted = completedSet.contains(todayStr)
        
        if (isTodayCompleted) {
            while (true) {
                val dateStr = sdf.format(calendar.time)
                if (completedSet.contains(dateStr)) {
                    streak++
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(calendar.time)
            if (completedSet.contains(yesterdayStr)) {
                while (true) {
                    val dateStr = sdf.format(calendar.time)
                    if (completedSet.contains(dateStr)) {
                        streak++
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                    } else {
                        break
                    }
                }
            } else {
                streak = 0
            }
        }
        return streak
    }
}
