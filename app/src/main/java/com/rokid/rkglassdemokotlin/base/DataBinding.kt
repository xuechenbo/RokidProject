package com.rokid.rkglassdemokotlin.base

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.rokid.logger.RKLogger

/**
 * Data binding
 * Data binding usage to bind value and views.
 */
object DataBinding {

    /**
     * Get status bar height
     * Method to get height value of status bar.
     * @param context
     * @return pixel: status bar height if resourceId "status_bar_height" value exist, else 24dp.
     */
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        }else{
            dip2px(context, 24f)
        }
    }

    /**
     * Px2dip
     * Method to calculate pixels to dp value.
     * @param context [Context] to get density
     * @param pxValue values to calculate
     * @return dp value
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * Dip2px
     * Method to calculate dp value to actual pixels displayed on screen.
     * @param context [Context] to get density
     * @param dipValue values to calculate
     * @return pixels value
     */
    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * Set view height
     * Method provided as 'app:setViewHeight' as a BindingAdapter to set the height of Views.
     * @param view The [View] to set height.
     * @param height value to set in dp mode.
     */
    @JvmStatic
    @BindingAdapter("setViewHeight")
    fun setViewHeight(view: View, height: Int?) {
        height?.let {
            view.layoutParams = view.layoutParams.apply {
                this.height = it
            }
        }
    }

    /**
     * Set text src
     * Method provided as 'app:setTextSrc' as a BindingAdapter to set [TextView]'s content by using a resourceId.
     * @param view [View] but only [TextView] which will be set the display content.
     * @param textSrc resourceId but not 0
     */
    @JvmStatic
    @BindingAdapter("setTextSrc")
    fun setTextSrc(view: View, textSrc: Int?) {
        textSrc?.let {
            if (view is TextView && it != 0){
                view.setText(it)
            }
        }
    }

    /**
     * Show view
     * Method provided as 'app:showView' as a BindingAdapter to set [View]'s visibility by using a Boolean value.
     * @param v [View] to set visibility.
     * @param toShow null to remove view from display, true to show the view, false to hide the view.
     */
    @JvmStatic
    @BindingAdapter("showView")
    fun showView(v: View, toShow: Boolean?){
        v.visibility = toShow?.let {
            if (it) View.VISIBLE else View.INVISIBLE
        }?:run{
            View.GONE
        }
    }

    /**
     * Enable view
     * Method provided as 'app:showView' as a BindingAdapter to set [View]'s enable and clickable value by using a Boolean value, also view's display alpha will be changed.
     * @param v [View] to change the enable value.
     * @param enable true to set [View] clickable and full alpha shown, false to set [View] unClickable and half alpha shown.
     */
    @JvmStatic
    @BindingAdapter("enableView")
    fun enableView(v: View, enable: Boolean?){
        v.isEnabled = enable == true
        v.isClickable = enable == true

        v.alpha = if (enable == true) 1.0f else 0.5f
    }

    /**
     * Set Img src
     * Method provided as 'app:setImgSrc' as a BindingAdapter to set [ImageView]'s content by using a resourceId.
     * @param v [View] but only [ImageView] which will be set the display content.
     * @param imgSrc resourceId but not 0
     */
    @JvmStatic
    @BindingAdapter("setImgSrc")
    fun setImgSrc(v: View, imgSrc: Int?){
        imgSrc?.let {
            if (v is ImageView && it != 0){
                v.setImageResource(it)
            }
        }
    }
}