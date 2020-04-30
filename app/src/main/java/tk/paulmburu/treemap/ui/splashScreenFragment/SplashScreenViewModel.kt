package tk.paulmburu.treemap.ui.splashScreenFragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tk.paulmburu.moviesreview.database.getDatabase
import tk.paulmburu.treemap.repository.TreesRepository

class SplashScreenViewModel(application: Application): ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    private val database = getDatabase(application)
    private val treesRepository = TreesRepository(database)

    init {
        coroutineScope.launch {
//            treesRepository.refreshTrees()
        }
//        Log.d("BUBA","${treesRepository.trees.value}")
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SplashScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SplashScreenViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}