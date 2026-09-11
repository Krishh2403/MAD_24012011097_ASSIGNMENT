package com.example.mad_24012011097_assignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FarmViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = FarmDatabase.getDatabase(application).farmDao()

    private var currentLogsSource: LiveData<List<FarmLog>>? = null

    val allLogs = MediatorLiveData<List<FarmLog>>().apply {
        value = emptyList()
    }

    fun setCurrentOwner(ownerId: String) {
        currentLogsSource?.let { oldSource ->
            allLogs.removeSource(oldSource)
        }

        if (ownerId.isBlank()) {
            allLogs.value = emptyList()
            currentLogsSource = null
            return
        }

        val newSource = dao.getLogsForOwner(ownerId)
        currentLogsSource = newSource

        allLogs.addSource(newSource) { logs ->
            allLogs.value = logs
        }
    }

    fun addLog(log: FarmLog) {
        viewModelScope.launch {
            dao.insertLog(log)
        }
    }

    fun deleteLog(log: FarmLog) {
        viewModelScope.launch {
            dao.deleteLog(log)
        }
    }
}