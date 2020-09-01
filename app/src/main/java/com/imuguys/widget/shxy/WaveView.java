package com.imuguys.widget.shxy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * 波浪View
 */
public class WaveView extends View {
  private static final String TAG = "WaveView";
  private Paint mPaint; // 画笔
  private Path mPath = new Path(); // 绘制路径
  private Path mOneCyclePath = new Path();
  private Path mTempPath = new Path(); // 绘制其他周期性路径
  private float mWaveHeight = 80f;
  private float mWaveWidth; // 波浪一个周期的宽度
  private float mBaseHeight; // 波浪y轴中心位置
  private Matrix mMatrix = new Matrix(); // 矩阵，用来平移Path
  private long mLastOnDrawTime = -1L; // 上次OnDraw时间，用来计算动画位置
  private float mTransform; // 已经平移了的位置，会累积
  private long mCycleMs = 1000; // 播放一个周期的时间，毫秒
  private int mCycleCount = 2; // 从x=0起，右边有多少个周期，由mWaveWidth于View宽度计算而来
  @ColorInt
  private int mWaveColor = Color.parseColor("#80DEEA");

  public WaveView(Context context) {
    this(context, null);
  }

  public WaveView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initPaint();
  }

  private void initPaint() {
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setDither(true);
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setColor(mWaveColor);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    // 默认 基线在View中线，波浪宽度等于View宽度
    mBaseHeight = getHeight() >> 1;
    mWaveWidth = getWidth();
    mCycleCount = (int) (Math.ceil(1.0f * w / mWaveWidth));
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    updateTransform();
    // 平移画布，动画效果
    canvas.translate(mTransform, 0);

    // 1/4周期，用来设置控制点
    float quarterX = mWaveWidth / 4;

    mPath.reset();
    mOneCyclePath.reset();
    // 先绘制左边不可见的一个周期
    mOneCyclePath.moveTo(-mWaveWidth, mBaseHeight);
    mOneCyclePath.quadTo(-quarterX * 3, mBaseHeight + mWaveHeight, -quarterX * 2, mBaseHeight);
    mOneCyclePath.quadTo(-quarterX, mBaseHeight - mWaveHeight, 0, mBaseHeight);
    mPath.addPath(mOneCyclePath);

    float cycleTransform = 0; // 平移距离
    // 平移拼接Path
    for (int i = 0; i < mCycleCount; i++) {
      cycleTransform = mWaveWidth * (1 + i);
      mTempPath.reset();
      mTempPath.addPath(mOneCyclePath);
      mMatrix.reset();
      mMatrix.preTranslate(cycleTransform, 0);
      mTempPath.transform(mMatrix);
      mPath.addPath(mTempPath);
    }

    // 封闭
    mPath.lineTo(mWaveWidth * mCycleCount, getHeight());
    mPath.lineTo(-mWaveWidth, getHeight());
    mPath.lineTo(-mWaveWidth, mBaseHeight);
    mPath.close();

    // 画全路径
    canvas.drawPath(mPath, mPaint);
    // 恢复画布位置
    canvas.translate(-mTransform, 0);
    // 更新时间，准备下次绘制
    mLastOnDrawTime = AnimationUtils.currentAnimationTimeMillis();
    invalidate();
  }

  /**
   * 更新 当前的transform
   */
  private void updateTransform() {
    // 跟随时间偏移
    if (mLastOnDrawTime == -1) {
      mTransform = 0f;
    } else {
      mTransform += ((AnimationUtils.currentAnimationTimeMillis() - mLastOnDrawTime) * 1.0f /
          mCycleMs * mWaveWidth);
    }
    // 不超过波浪宽度
    mTransform %= mWaveWidth;
  }

  /**
   * 设置播放一个周期(一个波峰波谷)所用的时间
   */
  public void setCycleMs(long cycleMs) {
    mCycleMs = cycleMs;
  }

  /**
   * 设置波峰、波谷的高度
   */
  public void setWaveHeight(float waveHeight) {
    mWaveHeight = waveHeight;
  }

  /**
   * 设置波浪颜色
   */
  public void setWaveColor(int waveColor) {
    mWaveColor = waveColor;
  }
}
