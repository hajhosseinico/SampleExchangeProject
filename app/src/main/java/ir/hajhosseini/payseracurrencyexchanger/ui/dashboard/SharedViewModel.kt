package ir.hajhosseini.payseracurrencyexchanger.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.hajhosseini.payseracurrencyexchanger.repository.MainRepository
import javax.inject.Inject

@HiltViewModel
class SharedViewModel
@Inject
constructor() : ViewModel() {
    private val _clickedPosition: MutableLiveData<Int> =
        MutableLiveData()

    val clickedPosition: LiveData<Int>
        get() = _clickedPosition

    fun setClickedPosition( pos: Int){
        _clickedPosition.postValue(pos)
    }

}


