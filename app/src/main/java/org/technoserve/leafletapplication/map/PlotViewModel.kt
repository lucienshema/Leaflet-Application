package org.technoserve.leafletapplication.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlotViewModel(application: Application) : AndroidViewModel(application) {
    private val plotDao = PlotDatabase.getDatabase(application).plotDao()

    val allPlots: LiveData<List<PlotEntity>> = liveData {
        val plots = withContext(Dispatchers.IO) {
            plotDao.getAllPlots() // Fetch data in a background thread
        }
        emit(plots)
    }
}

