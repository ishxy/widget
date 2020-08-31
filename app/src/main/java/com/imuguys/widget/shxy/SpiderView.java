package com.imuguys.widget.shxy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.google.common.base.Preconditions;

/**
 * 蛛网View
 */
public class SpiderView extends View {
  public static final String TAG = "SpiderView";
  private static final int DEFAULT_MAX_FLING = 10000;
  private static final int DEFAULT_AREA_STROKE_WIDTH = 5;
  private Paint mSpiderPaint; // 蛛网画笔
  private Paint mSpiderAreaPaint; // 蛛网区域画笔
  private Paint mAreaPaint; // 区域画笔
  private int mPointCount; // 定点个数
  private float mAngle; // 角度 六边形->60
  private int mCenterX;
  private int mCenterY;
  private Path mPath = new Path(); // 路径
  private float mRadius; // 内接圆半径
  private int mLevel; // 层数
  private float[] mPercentArray; // 每个方向的百分比
  private PointF[] mOuterPointsArray; // 外圈顶点位置
  private boolean mShouldRefreshPointData; // onSizeChanged触发，需要重新计算各点位置
  @ColorInt
  private int mSpiderLineColor;
  @ColorInt
  private int mAreaColor;
  @ColorInt
  private int[] mSpiderAreaColors; // 蛛网区域颜色
  private float mLastTouchX;
  private float mLastTouchY;
  private float mRotation; // 旋转角度
  // todo Scroller 太生硬了，ValueAnimator + Interpolator?
  private OverScroller mOverScroller; // fling Scroller
  private VelocityTracker mVelocityTracker;
  private float mFlingLastX; // fling时，上次x坐标
  private float mFlingLastY; // fling时，上次y坐标
  private int mMaxFling = DEFAULT_MAX_FLING; // 最大fling值

  public SpiderView(Context context) {
    this(context, null);
  }

  public SpiderView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SpiderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initPaint();
    mOverScroller = new OverScroller(context);
    mOverScroller.setFriction(ViewConfiguration.getScrollFriction());
    enableDefaultData();
  }

  private void initPaint() {
    mSpiderPaint = new Paint();
    mSpiderPaint.setStyle(Paint.Style.STROKE);
    mSpiderPaint.setAntiAlias(true);
    mSpiderPaint.setDither(true);
    mSpiderPaint.setColor(mSpiderLineColor);

    mSpiderAreaPaint = new Paint();
    mSpiderAreaPaint.setStyle(Paint.Style.FILL);
    mSpiderAreaPaint.setAntiAlias(true);
    mSpiderAreaPaint.setDither(true);

    mAreaPaint = new Paint();
    mAreaPaint.setStyle(Paint.Style.STROKE);
    mAreaPaint.setAntiAlias(true);
    mAreaPaint.setDither(true);
    mAreaPaint.setColor(mAreaColor);
    mAreaPaint.setStrokeWidth(DEFAULT_AREA_STROKE_WIDTH);
  }

  /**
   * 默认数据
   */
  private void enableDefaultData() {
    int pointCount = 6;
    float[] percentArray = new float[pointCount];
    for (int i = 0; i < pointCount; i++) {
      percentArray[i] = 0.15f * (i + 1);
    }
    int level = 5;
    int[] spiderColorArray = new int[level];
    spiderColorArray[0] = Color.parseColor("#80DEEA");
    spiderColorArray[1] = Color.parseColor("#26C6DA");
    spiderColorArray[2] = Color.parseColor("#00ACC1");
    spiderColorArray[3] = Color.parseColor("#00838F");
    spiderColorArray[4] = Color.parseColor("#006064");
    setPointCount(6, percentArray);
    setLevel(level, spiderColorArray);
    setSpiderLineColor(Color.WHITE);
    setAreaColor(Color.parseColor("#F44336"));
    setRotation(30);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mRadius = (float) (Math.min(w, h) * 0.9 / 2);
    mCenterX = w >> 1;
    mCenterY = h >> 1;
    mShouldRefreshPointData = true;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.translate(mCenterX, mCenterY);
    canvas.rotate(mRotation);
    drawSpider(canvas);
    if (mPercentArray != null) {
      drawArea(canvas);
    }
    canvas.rotate(mRotation);
    canvas.translate(-mCenterX, -mCenterY);
  }

  private void drawArea(Canvas canvas) {
    mPath.reset();
    for (int i = 0; i < mPointCount; i++) {
      if (i == 0) {
        mPath.moveTo(mOuterPointsArray[i].x * mPercentArray[i],
            mOuterPointsArray[i].y * mPercentArray[i]);
      } else {
        mPath.lineTo(mOuterPointsArray[i].x * mPercentArray[i],
            mOuterPointsArray[i].y * mPercentArray[i]);
      }
    }
    mPath.close();
    canvas.drawPath(mPath, mAreaPaint);
  }

  private void drawSpider(Canvas canvas) {
    long cast = System.nanoTime();
    float x;
    float y;
    float currentAngle = 0;
    float currentRadius = mRadius;

    for (int i = 0; i < mLevel; i++) { // 绘制每一圈
      for (int j = 0; j < mPointCount; j++) { // 绘制某一圈
        if (j == 0) {
          x = 0;
          y = -currentRadius;
          mPath.moveTo(x, y);
        } else {
          x = (int) (Math.sin(Math.toRadians(currentAngle)) * currentRadius);
          y = -(int) (Math.cos(Math.toRadians(currentAngle)) * currentRadius);
          mPath.lineTo(x, y);
        }
        if (i == 0) { // 最外圈，记录中心到顶点的线
          if (mShouldRefreshPointData) { // 需要更新
            mOuterPointsArray[j] = new PointF(x, y);
          }
        }
        currentAngle += mAngle;
      }
      currentRadius -= mRadius / mLevel;
      mPath.close();
      canvas.drawPath(mPath, mSpiderPaint);
      if (mSpiderAreaColors != null) {
        mSpiderAreaPaint.setColor(mSpiderAreaColors[i]);
        canvas.drawPath(mPath, mSpiderAreaPaint);
      }
      mPath.reset();
    }

    // 绘制中心点到边缘点的线
    for (int i = 0; i < mPointCount; i++) {
      canvas.drawLine(0, 0, mOuterPointsArray[i].x, mOuterPointsArray[i].y, mSpiderPaint);
    }
    Log.i(TAG, "onDraw cast = " + (System.nanoTime() - cast) + " ns");
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        initVelocityTracker();
        if (!mOverScroller.isFinished()) {
          mOverScroller.abortAnimation();
        }
        mVelocityTracker.addMovement(event);
        mLastTouchX = event.getX();
        mLastTouchY = event.getY();
        break;
      case MotionEvent.ACTION_MOVE:
        mVelocityTracker.addMovement(event);
        // 变换坐标系后的坐标值
        float lastX = mLastTouchX - mCenterX;
        float lastY = mLastTouchY - mCenterY;
        float currentX = event.getX() - mCenterX;
        float currentY = event.getY() - mCenterY;
        // A小角 B大角
        float angleA = getAngle(lastX, lastY);
        float angleB = getAngle(currentX, currentY);
        // 累积旋转角度
        mRotation += (angleB - angleA);
        mRotation %= 360;
        Log.i(TAG, "onTouchEvent MOVE : angleA = " + angleA + ", angleB = " + angleB);
        invalidate();
        mLastTouchX = event.getX();
        mLastTouchY = event.getY();
        break;
      case MotionEvent.ACTION_UP:
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1000);
        mFlingLastX = event.getX();
        mFlingLastY = event.getY();
        float xVelocity = mVelocityTracker.getXVelocity();
        float yVelocity = mVelocityTracker.getYVelocity();
        mOverScroller.fling(
            (int) event.getX(),
            (int) event.getY(),
            (int) xVelocity,
            (int) yVelocity,
            -mMaxFling,
            mMaxFling,
            -mMaxFling,
            mMaxFling);
        mVelocityTracker.recycle();
        mVelocityTracker = null;
        break;
    }
    return true;
  }

  @Override
  public void computeScroll() {
    super.computeScroll();
    if (mOverScroller.computeScrollOffset()) {
      // 转换坐标系
      float lastX = mFlingLastX - mCenterX;
      float lastY = mFlingLastY - mCenterY;
      float angleA = getAngle(lastX, lastY);

      // 转换坐标系
      float currentX = mOverScroller.getCurrX() - mCenterX;
      float currentY = mOverScroller.getCurrY() - mCenterY;
      float angleB = getAngle(currentX, currentY);

      // 累积旋转值
      mRotation += (angleB - angleA);
      invalidate();
      mFlingLastX = mOverScroller.getCurrX();
      mFlingLastY = mOverScroller.getCurrY();
    }
  }

  private void initVelocityTracker() {
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    } else {
      mVelocityTracker.clear();
    }
  }

  private float getAngle(float x, float y) {
    float l = (float) Math.sqrt(x * x + y * y);
    float a = (float) Math.acos(x / l);
    float ret = (float) Math.toDegrees(a);
    if (y < 0) {
      return 360 - ret;
    }
    return ret;
  }

  /**
   * 设置蛛网颜色
   */
  public void setSpiderLineColor(@ColorInt int spiderLineColor) {
    mSpiderLineColor = spiderLineColor;
    mSpiderPaint.setColor(spiderLineColor);
  }

  /**
   * 设置区域颜色
   */
  public void setAreaColor(@ColorInt int areaColor) {
    mAreaColor = areaColor;
    mAreaPaint.setColor(areaColor);
  }

  /**
   * 设置最大fling
   */
  public void setMaxFling(int maxFling) {
    mMaxFling = maxFling;
  }

  /**
   * 设置蛛网顶点数量
   */
  public void setPointCount(@IntRange(from = 3) int pointCount, @Nullable float[] percentArray) {
    Preconditions.checkArgument(pointCount >= 3);
    if (percentArray != null) {
      Preconditions.checkState(percentArray.length == pointCount);
    }
    mPointCount = pointCount;
    mAngle = 360f / mPointCount;
    mPercentArray = percentArray;
    mOuterPointsArray = new PointF[mPointCount];
  }

  /**
   * 设置蛛网层级
   * 设置每条边的百分比，从最上面 顺时针设置
   */
  public void setLevel(@IntRange(from = 1) int level, @Nullable int[] spiderAreaColors) {
    Preconditions.checkArgument(level >= 1);
    if (spiderAreaColors != null) {
      Preconditions.checkState(spiderAreaColors.length == level);
    }
    mLevel = level;
    mSpiderAreaColors = spiderAreaColors;
  }

  public void setAreaStrokeWidth(int width) {
    mSpiderAreaPaint.setStrokeWidth(width);
  }

  @Override
  public float getRotation() {
    return mRotation;
  }

  @Override
  public void setRotation(float rotation) {
    mRotation = rotation;
  }
}
