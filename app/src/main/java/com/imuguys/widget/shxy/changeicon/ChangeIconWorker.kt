package com.imuguys.widget.shxy.changeicon

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class ChangeIconWorker : Worker {
  private val context : Context
  private val workerParameters : WorkerParameters
  constructor(context: Context, workerParams: WorkerParameters) : super(context, workerParams) {
    this.context = context
    this.workerParameters = workerParams
  }

  override fun doWork(): Result {
    Log.i("ChangeIconWorker", "doWork")
    LauncherIconUtils.enableNewStyleLauncherIcon(context)
    return Result.success()
  }

  override fun onStopped() {
    super.onStopped()
    Log.i("ChangeIconWorker", "onStopped")
  }
}