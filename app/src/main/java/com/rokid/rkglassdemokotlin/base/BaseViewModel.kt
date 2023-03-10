package com.rokid.rkglassdemokotlin.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Base view model
 * Based on [ViewModel], include a [MutableLiveData] which can be observed by activities.
 * @constructor
 */
open class BaseViewModel : ViewModel() {
    //event data
    val event: MutableLiveData<BaseEvent> = MutableLiveData()

}