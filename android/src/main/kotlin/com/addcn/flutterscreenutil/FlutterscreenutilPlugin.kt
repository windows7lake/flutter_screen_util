package com.addcn.flutterscreenutil

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.NonNull;
import com.gyf.immersionbar.ImmersionBar
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.lang.Exception
import java.lang.ref.WeakReference
import kotlin.math.abs
import kotlin.math.roundToInt

/** FlutterScreenUtilPlugin */
public class FlutterScreenUtilPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

    private var activityRef: WeakReference<Activity>? = null

    constructor()
    constructor(activity: Activity) {
        this.activityRef = WeakReference(activity)
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        val channel = MethodChannel(flutterPluginBinding.flutterEngine.dartExecutor, "flutterscreenutil")
        channel.setMethodCallHandler(this)
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutterscreenutil")
            channel.setMethodCallHandler(FlutterScreenUtilPlugin(registrar.activity()))
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getNavigationBarHeight") {
            val activity = activityRef?.get()
            if (activity == null) {
                result.success(0)
                return
            }
            val bottomBarHeight = getVirtualBottomBarHeight(activity) - ImmersionBar.getNotchHeight(activity)
            if (abs(bottomBarHeight - ImmersionBar.getNavigationBarHeight(activity)) < 5) { // 误差范围在 5 以内
                result.success(0)
                return
            }
            result.success(px2dp(bottomBarHeight.toFloat()))
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    }

    override fun onDetachedFromActivity() {
        activityRef?.clear()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityRef = WeakReference(binding.activity)
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }


    private fun getVirtualBottomBarHeight(activity: Activity): Int {
        val metrics = DisplayMetrics()
        // 这个方法获取可能不是真实屏幕的高度
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight: Int = metrics.heightPixels
        // 获取当前屏幕的真实高度
        val realHeight =
            if (Build.VERSION.SDK_INT < 17) {
                getRealScreenHeight(activity)
            } else {
                activity.windowManager.defaultDisplay.getRealMetrics(metrics)
                metrics.heightPixels
            }

        return if (realHeight > usableHeight) {
            realHeight - usableHeight
        } else {
            0
        }
    }

    private fun getRealScreenHeight(activity: Activity): Int {
        val display = activity.windowManager.defaultDisplay;
        val displayMetrics = DisplayMetrics()
        try {
            val clazz = Class.forName("android.view.Display")
            val method = clazz.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, displayMetrics)
        } catch (e: Exception) {
            return 0
        }
        return displayMetrics.heightPixels
    }

    private fun px2dp(px: Float): Int {
        val displayMetrics = Resources.getSystem().displayMetrics
        val dp = px / (displayMetrics.densityDpi / 160f)
        return dp.roundToInt()
    }
}
