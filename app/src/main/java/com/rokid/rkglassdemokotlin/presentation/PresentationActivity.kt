package com.rokid.rkglassdemokotlin.presentation

import android.os.Bundle
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.base.BaseActivity
import com.rokid.rkglassdemokotlin.base.DataBinding
import com.rokid.rkglassdemokotlin.databinding.ActivityPresentationBinding

class PresentationActivity : BaseActivity() {

    private lateinit var dataBinding: ActivityPresentationBinding
    private lateinit var viewModel: PresentationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityPresentationBinding.inflate(layoutInflater)
        dataBinding.lifecycleOwner = this
        viewModel = getViewModel(PresentationViewModel::class.java)
        initViewModel(viewModel)
        dataBinding.data = viewModel.getModel(DataBinding.getStatusBarHeight(this))
        setContentView(dataBinding.root)
    }
}