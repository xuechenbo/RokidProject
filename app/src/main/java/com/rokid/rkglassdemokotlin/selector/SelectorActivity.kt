package com.rokid.rkglassdemokotlin.selector

import android.os.Bundle
import com.rokid.rkglassdemokotlin.base.BaseActivity
import com.rokid.rkglassdemokotlin.base.BaseViewModel
import com.rokid.rkglassdemokotlin.base.DataBinding
import com.rokid.rkglassdemokotlin.databinding.ActivitySelectorBinding

/**
 * Selector activity
 * Menu of Testing items.
 * @constructor Create empty Selector activity
 */
class SelectorActivity : BaseActivity() {

    private lateinit var dataBinding: ActivitySelectorBinding
    private lateinit var viewModel: SelectorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivitySelectorBinding.inflate(layoutInflater)
        dataBinding.lifecycleOwner = this
        setContentView(dataBinding.root)
        viewModel = getViewModel(SelectorViewModel::class.java)
        initViewModel(viewModel)
        dataBinding.data = viewModel.getModel(DataBinding.getStatusBarHeight(this))
    }


}