package com.poker.jacksorbetter.training

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrainingViewModel : ViewModel() {


    val state: MutableLiveData<State> by lazy { MutableLiveData(State.START) }


    enum class State {
        START,
        DONE
    }


}