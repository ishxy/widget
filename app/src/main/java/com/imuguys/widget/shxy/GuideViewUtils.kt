package com.imuguys.widget.shxy

import android.view.View
import com.imuguys.widget.shxy.GuideView.TargetViewInformation

object GuideViewUtils {

  /**
   * @param targetView 被引导的View
   * @param charSequence 引导文案
   * @param guideView 引导View
   */
  @JvmStatic
  fun showGuide(targetView: View, charSequence: CharSequence, guideView: GuideView) {
    targetView.post {
      guideView.visibility = View.VISIBLE
      val targetViewInformation = TargetViewInformation()
      val position = IntArray(2)
      val width: Int = targetView.width
      val height: Int = targetView.height
      targetView.getLocationInWindow(position)
      targetViewInformation.setCenterXInWindow(position[0] + width / 2f)
      targetViewInformation.setCenterYInWindow(position[1] + height / 2f)
      targetViewInformation.setRadius(width.coerceAtLeast(height) / 2f + 10)
      guideView
          .setTargetViewInformationAndSimpleText(targetViewInformation, charSequence)
      guideView.invalidate()
    }
  }
}