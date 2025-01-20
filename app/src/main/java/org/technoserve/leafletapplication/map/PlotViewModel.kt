package org.technoserve.leafletapplication.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlotViewModel(application: Application) : AndroidViewModel(application) {
//    private val plotDao = PlotDatabase.getDatabase(application).plotDao()
//    val allPlots: LiveData<List<PlotEntity>> = liveData {
//        val plots = withContext(Dispatchers.IO) {
//            plotDao.getAllPlots() // Fetch data in a background thread
//        }
//        emit(plots)
//    }

    private val plotDao = PlotDatabase.getDatabase(application).plotDao()

    val allPlots: LiveData<List<PlotEntity>> = plotDao.getAllPlots()

    fun insertPlot(plot: PlotEntity) {
        viewModelScope.launch {
            plotDao.insertPlot(plot)
        }
    }


}

