package com.imuguys.widget.shxy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.imuguys.widget.R;

/**
 * 引导View
 */
public class GuideView extends FrameLayout {
  public static final String TAG = "GuideView";
  @Nullable
  private TargetViewInformation mTargetViewInformation; // 被引导view信息
  @ColorInt
  private int mBackgroundColor = Color.parseColor("#cc212121"); // 背景颜色
  private int mLineColor = Color.parseColor("#ffffff"); // 线颜色
  private Paint mBackgroundPaint; // 背景画笔
  private Paint mLinePaint; // 线画笔
  private RectF mBackgroundRect = new RectF(); // 背景矩形
  private Path mPath = new Path(); // 目标view的裁剪路径
  private int mDotRadiusPx = 10; // 圆点半径
  private int mDotAndCircleMarginPx = 20; // 圆点与被引导View的间距
  private int mLineWidthPx = 5; // 线宽
  private int mLineLengthPx = 200; // 先长
  private View mTipView; // 提示View，默认会是一个TextView，对应 layout/simple_text_view.xml

  public GuideView(Context context) {
    this(context, null);
  }

  public GuideView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public GuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initPaint();
    setWillNotDraw(false);
    setVisibility(GONE);
  }

  private void initPaint() {
    mBackgroundPaint = new Paint();
    mBackgroundPaint.setColor(mBackgroundColor);
    mBackgroundPaint.setStyle(Paint.Style.FILL);
    mBackgroundPaint.setDither(true);
    mBackgroundPaint.setAntiAlias(true);

    mLinePaint = new Paint();
    mLinePaint.setColor(mLineColor);
    mLinePaint.setDither(true);
    mLinePaint.setAntiAlias(true);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    if (!hasNecessaryDrawInformation()) {
      return;
    }
    int halfTipViewMeasureWidth = mTipView.getMeasuredWidth() >> 1;
    int tipViewTop = (int) (mTargetViewInformation.mCenterY - mTargetViewInformation.mRadius -
        mDotAndCircleMarginPx - mLineLengthPx - mTipView.getMeasuredHeight());
    // todo 支持 START END BOTTOM
    if (mTargetViewInformation.mGravity == Gravity.TOP) {
      if (halfTipViewMeasureWidth > mTargetViewInformation.mCenterX) {
        // 需要居左
        mTipView.layout(
            0,
            tipViewTop,
            mTipView.getMeasuredWidth(),
            tipViewTop + mTipView.getMeasuredHeight());
      } else if (halfTipViewMeasureWidth > getMeasuredWidth() - mTargetViewInformation.mCenterX) {
        // 需要居右
        mTipView.layout(
            getMeasuredWidth() - mTipView.getMeasuredWidth(),
            tipViewTop,
            getMeasuredWidth(),
            tipViewTop + mTipView.getMeasuredHeight());
      } else {
        // 居中
        mTipView.layout(
            (int) (mTargetViewInformation.mCenterX - mTipView.getMeasuredWidth() / 2),
            tipViewTop,
            (int) (mTargetViewInformation.mCenterX + mTipView.getMeasuredWidth() / 2),
            tipViewTop + mTipView.getMeasuredHeight());
      }
    }
  }

  /**
   * 是否有绘制的必要信息
   */
  private boolean hasNecessaryDrawInformation() {
    return mTargetViewInformation != null && mTipView != null;
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mBackgroundRect.left = 0;
    mBackgroundRect.right = w;
    mBackgroundRect.top = 0;
    mBackgroundRect.bottom = h;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (!hasNecessaryDrawInformation()) {
      return;
    }
    canvas.save();
    canvas.clipPath(mPath, Region.Op.DIFFERENCE);
    canvas.drawRect(mBackgroundRect, mBackgroundPaint);
    drawLine(canvas);
    canvas.restore();
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    if (!hasNecessaryDrawInformation()) { // 没有目标信息，不绘制子View
      return;
    }
    super.dispatchDraw(canvas);
  }

  /**
   * 绘制 圆点和线
   */
  private void drawLine(Canvas canvas) {
    if (!hasNecessaryDrawInformation()) {
      return;
    }
    mLinePaint.setStyle(Paint.Style.FILL);
    canvas.drawCircle(
        mTargetViewInformation.mCenterX,
        mTargetViewInformation.mCenterY - mTargetViewInformation.mRadius - mDotAndCircleMarginPx,
        mDotRadiusPx,
        mLinePaint);
    mLinePaint.setStyle(Paint.Style.STROKE);
    mLinePaint.setStrokeWidth(mLineWidthPx);
    float startY = mTargetViewInformation.mCenterY - mTargetViewInformation.mRadius -
        mDotAndCircleMarginPx;
    canvas.drawLine(mTargetViewInformation.mCenterX, startY, mTargetViewInformation.mCenterX,
        startY - mLineLengthPx, mLinePaint);
  }

  /**
   * 设置被引导View信息和提示View
   *
   * @param targetViewInformation 被引导View信息
   * @param tipView               提示View
   * @param layoutParams          提示View的布局参数
   */
  public void setTargetViewInformationAndTipView(
      @NonNull TargetViewInformation targetViewInformation,
      @Nullable View tipView,
      @Nullable ViewGroup.LayoutParams layoutParams) {
    mTargetViewInformation = targetViewInformation;
    fixTargetViewInformationPosition();
    mPath.reset();
    mPath.addCircle(
        targetViewInformation.mCenterX,
        targetViewInformation.mCenterY,
        targetViewInformation.mRadius,
        Path.Direction.CW);
    Log.i(TAG, "setTargetViewInformation mTargetViewInformation = " + mTargetViewInformation);
    mPath.close();

    if (tipView == null) {
      mTipView = LayoutInflater.from(getContext()).inflate(R.layout.simple_text_view, this, false);
    } else {
      mTipView = tipView;
    }
    mTipView.setBackground(null);
    if (layoutParams != null) {
      addView(tipView, layoutParams);
    } else {
      addView(tipView);
    }
  }

  /**
   * 简单设置被引导的View信息以及引导文案
   *
   * @param targetViewInformation 被引导View信息
   * @param charSequence          引导文案
   */
  public void setTargetViewInformationAndSimpleText(
      @NonNull TargetViewInformation targetViewInformation,
      @NonNull CharSequence charSequence) {
    TextView simpleTextView =
        (TextView) LayoutInflater.from(getContext())
            .inflate(R.layout.simple_text_view, this, false);
    ((TextView) simpleTextView).setText(charSequence);
    setTargetViewInformationAndTipView(targetViewInformation, simpleTextView, null);
  }

  /**
   * 修正TargetViewInformationPosition中的x,y
   */
  private void fixTargetViewInformationPosition() {
    int[] location = new int[2];
    getLocationInWindow(location);
    mTargetViewInformation.fixPosition(location);
  }

  public int getBackgroundColor() {
    return mBackgroundColor;
  }

  public int getLineColor() {
    return mLineColor;
  }

  public int getDotRadiusPx() {
    return mDotRadiusPx;
  }

  public int getDotAndCircleMarginPx() {
    return mDotAndCircleMarginPx;
  }

  public int getLineWidthPx() {
    return mLineWidthPx;
  }

  public int getLineLengthPx() {
    return mLineLengthPx;
  }

  /**
   * 目标View信息
   */
  public static final class TargetViewInformation {
    private float mCenterXInWindow; // 中心点x在window中的位置
    private float mCenterYInWindow; // 中心点y在window中的位置
    private float mRadius; // 半径，一般取长、宽中更大的值

    private float mCenterX; // 中心点x在Parent中的位置
    private float mCenterY; // 中心点y在Parent中的位置
    private int mGravity = Gravity.TOP; // 引导文字方向，目前只支持向上

    public void setCenterXInWindow(float centerXInWindow) {
      mCenterXInWindow = centerXInWindow;
    }

    public void setCenterYInWindow(float centerYInWindow) {
      mCenterYInWindow = centerYInWindow;
    }

    public void setRadius(float radius) {
      mRadius = radius;
    }

    /**
     * 根据parent在window中的位置计算targetView在parent中的位置
     *
     * @param location parent在window中的位置
     */
    private void fixPosition(int[] location) {
      mCenterX = mCenterXInWindow - location[0];
      mCenterY = mCenterYInWindow - location[1];
    }

    @Override
    public String toString() {
      return "TargetViewInformation{" +
          "mCenterXInWindow=" + mCenterXInWindow +
          ", mCenterYInWindow=" + mCenterYInWindow +
          ", mRadius=" + mRadius +
          ", mCenterX=" + mCenterX +
          ", mCenterY=" + mCenterY +
          ", mGravity=" + mGravity +
          '}';
    }
  }
}
