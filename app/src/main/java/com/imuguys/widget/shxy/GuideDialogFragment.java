package com.imuguys.widget.shxy;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.imuguys.widget.R;
import com.imuguys.widget.databinding.GuideTestLayoutBinding;

/**
 * 引导用的DialogFragment
 */
public class GuideDialogFragment extends DialogFragment {

  private GuideTestLayoutBinding mGuideTestLayoutBinding;
  private View mTargetView;
  private CharSequence mGuideCharSequence;

  /**
   * 构造
   *
   * @param targetView   需要被引导的View
   * @param charSequence 文案
   */
  public static GuideDialogFragment newInstance(
      @NonNull View targetView,
      @NonNull CharSequence charSequence) {
    GuideDialogFragment fragment = new GuideDialogFragment();
    fragment.mTargetView = targetView;
    fragment.mGuideCharSequence = charSequence;
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.guide_test_layout, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mGuideTestLayoutBinding = DataBindingUtil.bind(view);
    mGuideTestLayoutBinding.guideView.setOnClickListener(v -> {
      // 点击后消失
      if (getFragmentManager() != null && isAdded()) {
        getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
      }
    });
    GuideViewUtils.showGuide(mTargetView, mGuideCharSequence, mGuideTestLayoutBinding.guideView);
  }

  @Override
  public void onStart() {
    super.onStart();
    Dialog dialog = getDialog();
    if (dialog == null || dialog.getWindow() == null || getContext() == null) {
      return;
    }
    WindowManager windowManager = getContext().getSystemService(WindowManager.class);
    Point pointSize = new Point();
    // 获取屏幕长宽
    windowManager.getDefaultDisplay().getRealSize(pointSize);
    int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    // 设置全屏属性
    dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    dialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
    dialog.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    dialog.getWindow().setLayout(pointSize.x, pointSize.y);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    return new Dialog(requireContext(), R.style.GuideDialog);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mTargetView = null;
  }
}
