package com.rokid.rkglassdemokotlin.presentation

import com.rokid.rkglassdemokotlin.base.BaseViewModel

class PresentationViewModel: BaseViewModel() {

    private lateinit var model: PresentationModel


    fun getModel(topHeight: Int): PresentationModel {

        model = PresentationModel{
            event.postValue(it)
        }

        return model
    }

}