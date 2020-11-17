package com.imuguys.widget.shxy.changeicon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * 图标替换工具类
 */
object LauncherIconUtils {
  private const val TAG = "LauncherIconUtils"
  private const val DEFAULT_MAIN_ACTIVITY = "com.imuguys.widget.MainActivity"
  private const val NEW_STYLE_MAIN_ACTIVITY = "com.imuguys.widget.NewStyleMainActivity"

  @JvmStatic
  fun test(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)  // 网络状态
        .setRequiresBatteryNotLow(true)                 // 不在电量不足时执行
        .build()
    val workRequest = OneTimeWorkRequest.Builder(
        ChangeIconWorker::class.java)
        .setInitialDelay(10,TimeUnit.SECONDS)
        .setConstraints(constraints)
        .build()
    WorkManager.getInstance().enqueueUniqueWork(
        "",
        ExistingWorkPolicy.KEEP,
        workRequest)
  }

  @JvmStatic
  fun enableNewStyleLauncherIcon(context: Context) {
    Log.i(TAG, "enableNewStyleLauncherIcon")
    val newStyleComponentName = ComponentName(context, NEW_STYLE_MAIN_ACTIVITY)
    val defaultComponentName = ComponentName(context, DEFAULT_MAIN_ACTIVITY)
    if (context.packageManager.getComponentEnabledSetting(newStyleComponentName) ==
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
      return
    }
    context.packageManager.setComponentEnabledSetting(
        newStyleComponentName,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP)
    context.packageManager.setComponentEnabledSetting(
        defaultComponentName,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP)
  }

  @JvmStatic
  fun disableNewStyleLauncherIcon(context: Context) {
    Log.i(TAG, "disableNewStyleLauncherIcon")
    val newStyleComponentName = ComponentName(context, NEW_STYLE_MAIN_ACTIVITY)
    val defaultComponentName = ComponentName(context, DEFAULT_MAIN_ACTIVITY)
    if (context.packageManager.getComponentEnabledSetting(newStyleComponentName) ==
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
      return
    }
    context.packageManager.setComponentEnabledSetting(
        newStyleComponentName,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP)
    context.packageManager.setComponentEnabledSetting(
        defaultComponentName,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP)
  }
}