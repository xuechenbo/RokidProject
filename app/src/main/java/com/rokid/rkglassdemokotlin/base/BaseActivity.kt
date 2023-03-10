package com.rokid.rkglassdemokotlin.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rokid.rkglassdemokotlin.R
import com.rokid.rkglassdemokotlin.app.RKApplication
import com.rokid.rkglassdemokotlin.main.MainActivity
import com.rokid.rkglassdemokotlin.selector.SelectorActivity

/**
 * Base activity
 * Because of glass test, this is the base activity made with listener of glass connection status changes.
 * Base activity with usage of [ViewModel] and [ViewDataBinding]
 * @constructor Basic activity of this project.
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * Get view model
     * Function to get ViewModel
     * @param T based on [BaseViewModel]
     * @param clazz the class to invoke.
     * @return [T]
     */
    fun <T : BaseViewModel> getViewModel(clazz: Class<T>): T = ViewModelProvider(this)[clazz]

    /**
     * Init view model
     * After get a [T] which based on [BaseViewModel], observer things provided in it.
     * @param T based on [BaseViewModel]
     * @param viewModel Instance of [T] get from [getViewModel]
     */
    fun <T : BaseViewModel> initViewModel(viewModel: T) {
        viewModel.event.observe(this) { event ->//Do event when any event post from viewModel.
            event?.let {
                it.doEvent(this)
                viewModel.event.postValue(null)
            }
        }
        //listen to the connect status in every activity.
        RKApplication.INSTANCE.connectStatus.observe(this) { aBoolean ->//when connected, aBoolean value will be true. when lost connection, aBoolean value will be false.
            aBoolean?.let {
                if (this !is MainActivity && !it) {//if disconnect start MainActivity
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                } else if (this is MainActivity && it) {// if connected, start SelectorActivity.
                    startActivity(Intent(this, SelectorActivity::class.java))
                    this.finish()
                }
                RKApplication.INSTANCE.connectStatus.postValue(null)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}