package com.rokid.rkglassdemokotlin.base

import android.content.Context

/**
 * Base event
 * event which post from every where which can be caught by BaseActivity when using MVVM.
 * @constructor Create empty Base event
 */
open class BaseEvent {
    /**
     * Do event
     * override this function to post things to do.
     * @param context
     */
    open fun doEvent(context: Context){}

}