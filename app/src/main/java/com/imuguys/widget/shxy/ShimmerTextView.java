package com.imuguys.widget.shxy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 颜色渐变、颜色滚动的TextView
 */
public class ShimmerTextView extends AppCompatTextView {
  private static final String TAG = "ShimmerTextView";
  private static final int SEGMENT_COUNT = 2; // 需要绘制段数
  private Paint mBackgroundPaint;
  private Paint mColorsPaint;
  private RectF mBackgroundRectF = new RectF();
  private RectF mColorsRectF = new RectF();
  private boolean mIsAnimationStopped = true;
  private float mColorsRectTranslate; // 颜色矩形的平移距离
  private long mLastAnimationTimeStamp = -1;
  private long mCycleMs = 1000; // 由颜色A->B->A花费的时间
  private int[] mTextColors = new int[] {
      Color.RED,
      Color.WHITE, Color.GREEN, Color.YELLOW,
      Color.RED,
      Color.WHITE, Color.GREEN, Color.YELLOW,
      Color.RED};

  public ShimmerTextView(Context context) {
    this(context, null);
  }

  public ShimmerTextView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ShimmerTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mBackgroundPaint = new Paint();
    mBackgroundPaint.setColor(Color.WHITE);
    Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    getPaint().setXfermode(xfermode);
    mColorsPaint = new Paint();
    setTextColor(Color.TRANSPARENT);
    setTextColors(new int[] {Color.RED, Color.WHITE, Color.GREEN, Color.YELLOW, Color.BLUE});
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    LinearGradient linearGradient = new LinearGradient(
        0,
        0,
        w * SEGMENT_COUNT,
        0,
        mTextColors,
        null,
        Shader.TileMode.CLAMP);
    mColorsPaint.setShader(linearGradient);
    mBackgroundRectF.left = 0;
    mBackgroundRectF.top = 0;
    mBackgroundRectF.right = w;
    mBackgroundRectF.bottom = h;

    mColorsRectF.left = 0;
    mColorsRectF.top = 0;
    mColorsRectF.right = w * SEGMENT_COUNT;
    mColorsRectF.bottom = h;

    Log.i(TAG, "w = " + w);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    // 计算本次绘制的位移
    updateColorsRectTranslate();
    // 防止clipChild = false
    canvas.clipRect(mBackgroundRectF);
    float translateX = mColorsRectTranslate;
    // 向左移动一个周期
    canvas.translate(-getMeasuredWidth() * (SEGMENT_COUNT - 1) + translateX, 0);
    // 绘制"字体颜色"矩形
    canvas.drawRect(mColorsRectF, mColorsPaint);
    canvas.translate(getMeasuredWidth() * (SEGMENT_COUNT - 1) - translateX, 0);
    canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), null);
    // 绘制背景颜色
    canvas.drawRect(mBackgroundRectF, mBackgroundPaint);
    // 用SRC_OUT将文字位置镂空，漏出下面的"字体颜色"
    super.onDraw(canvas);
    canvas.restore();
    if (!mIsAnimationStopped) {
      invalidate();
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    startAnimation();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mIsAnimationStopped = true;
  }

  private void updateColorsRectTranslate() {
    if (mLastAnimationTimeStamp == -1) {
      mColorsRectTranslate = 0f;
    } else {
      mColorsRectTranslate +=
          ((AnimationUtils.currentAnimationTimeMillis() - mLastAnimationTimeStamp) * 1.0f / mCycleMs
              * (getMeasuredWidth() * (SEGMENT_COUNT - 1)));
    }
    // 更新时间
    mLastAnimationTimeStamp = AnimationUtils.currentAnimationTimeMillis();
    // 更新x位移
    mColorsRectTranslate %= getMeasuredWidth() * (SEGMENT_COUNT - 1);
  }

  public void setTextColors(@NonNull int[] textColors) {
    mTextColors = new int[textColors.length * SEGMENT_COUNT + 1];
    for (int i = 0; i < SEGMENT_COUNT; i++) {
      System.arraycopy(textColors, 0, mTextColors, i * textColors.length, textColors.length);
    }
    mTextColors[textColors.length * SEGMENT_COUNT] = textColors[0];
  }

  public void setBackgroundRectColor(int color) {
    mBackgroundPaint.setColor(color);
  }

  public void startAnimation() {
    mIsAnimationStopped = false;
    mLastAnimationTimeStamp = -1;
    invalidate();
  }

  public void stopAnimation() {
    mIsAnimationStopped = true;
    mLastAnimationTimeStamp = -1;
  }

  public void setCycleMs(long cycleMs) {
    mCycleMs = cycleMs;
  }
}
