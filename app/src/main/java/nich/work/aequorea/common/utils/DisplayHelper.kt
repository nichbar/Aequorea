package nich.work.aequorea.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.core.widget.NestedScrollView
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import nich.work.aequorea.R
import nich.work.aequorea.common.ui.activity.BaseActivity
import nich.work.aequorea.common.ui.widget.StatusBarView

object DisplayHelper {

    fun getStatusBarHeight(r: Resources): Int {
        var result = 0
        val resourceId = r.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = r.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun setStatusBarStyle(activity: Activity, statusBarView: StatusBarView? = null, showLightStatusBar: Boolean) {
        val isMIUI = setMIUIStatusBarStyle(activity, showLightStatusBar)

        if (showLightStatusBar) {
            statusBarView?.setLightMask()
        } else {
            statusBarView?.setDarkMask()
        }

        if (!isMIUI) {
            val window = activity.window
            val decor = window.decorView

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && showLightStatusBar) {
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }
    }

    fun setStatusInLowProfileMode(activity: BaseActivity, isLight: Boolean) {
        val decor = activity.window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLight) {
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LOW_PROFILE
            } else {
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LOW_PROFILE
            }
        } else {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LOW_PROFILE
        }
    }

    @SuppressLint("PrivateApi")
    private fun setMIUIStatusBarStyle(activity: Activity, isLightStatusBar: Boolean): Boolean {
        var result = false
        val window = activity.window
        if (window != null) {
            val clazz = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                if (isLightStatusBar) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
                }
                result = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (isLightStatusBar) {
                        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                }
            } catch (e: Exception) {
                // Ignore this useless reflect exception.
            }

        }
        return result
    }

    fun cancelTranslucentNavigation(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }

    fun dp2px(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.applicationContext.resources.displayMetrics).toInt()
    }

    fun sp2px(context: Context, sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.applicationContext.resources.displayMetrics).toInt()
    }

    // Take a screen shot of the given nestedScrollView.
    fun shotNestedScrollView(nestedScrollView: NestedScrollView, backgroundColor: Int): Bitmap {
        var recHeight = 0
        val childView = nestedScrollView.getChildAt(0)
        val recommendationView = nestedScrollView.findViewById<View>(R.id.container_recommendation)
        val subRecommendationView = nestedScrollView.findViewById<ViewGroup>(R.id.container_recommendation_sub)

        if (subRecommendationView != null && subRecommendationView.childCount > 0) {
            recHeight = recommendationView.height
        }

        childView.setBackgroundColor(backgroundColor)

        val bitmap = Bitmap.createBitmap(nestedScrollView.width, childView.height - recHeight, Bitmap.Config.RGB_565)

        val canvas = Canvas(bitmap)
        nestedScrollView.draw(canvas)

        childView.setBackgroundColor(Color.TRANSPARENT)

        return bitmap
    }
}