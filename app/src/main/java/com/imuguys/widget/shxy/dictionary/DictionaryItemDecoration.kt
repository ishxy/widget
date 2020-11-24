package com.imuguys.widget.shxy.dictionary

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class DictionaryItemDecoration : RecyclerView.ItemDecoration() {

  private val headerHeight = 100
  private val dividerHeight = 1
  private val dividerPaint = Paint()
  private val textPaint = TextPaint()
  private val headerPaint = Paint()
  private val headerPaddingLeft = 30f

  init {
    dividerPaint.color = Color.parseColor("#BDBDBD")
    textPaint.color = Color.parseColor("#212121")
    textPaint.isDither = true
    textPaint.isAntiAlias = true
    textPaint.textSize = 30f
    headerPaint.color = Color.parseColor("#40C4FF")
  }

  override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDrawOver(c, parent, state)
    val adapter = parent.adapter as? IDictionaryAdapter ?: return
    val linearLayoutManager = parent.layoutManager as? LinearLayoutManager ?: return
    val position = linearLayoutManager.findFirstVisibleItemPosition()
    val view = parent.findViewHolderForAdapterPosition(position)?.itemView ?: return
    val isNextItemHeader = adapter.isItemPositionHeader(position + 1)
    if (isNextItemHeader) {
      val scrollY = max(headerHeight - view.bottom, 0)
      drawFakeHeader(c, adapter, parent, position, scrollY)
    } else {
      drawFakeHeader(c, adapter, parent, position, 0)
    }
  }

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDraw(c, parent, state)
    val adapter = parent.adapter as? IDictionaryAdapter ?: return
    for (i in 0 until parent.childCount) {
      val childView = parent.getChildAt(i)
      val childLayoutPosition = parent.getChildLayoutPosition(childView)
      if (adapter.isItemPositionHeader(childLayoutPosition)) {
        drawHeader(c, childView, parent, adapter, childLayoutPosition)
      } else {
        drawDivider(c, childView, parent)
      }
    }
  }

  private fun drawDivider(c: Canvas, childView: View, parent: RecyclerView) {
    c.drawRect(
        0f,
        childView.bottom.toFloat(),
        parent.width.toFloat(),
        (childView.bottom + dividerHeight).toFloat(),
        dividerPaint)
  }

  /**
   * 为每个组的第一个item绘制header
   */
  private fun drawHeader(
      c: Canvas,
      childView: View,
      parent: RecyclerView,
      adapter: IDictionaryAdapter,
      childLayoutPosition: Int) {
    c.drawRect(
        0f,
        (childView.top - headerHeight).toFloat(),
        parent.width.toFloat(),
        childView.top.toFloat(),
        headerPaint)
    val text = adapter.getItemPositionHeaderText(childLayoutPosition)
    val fontMetrics = textPaint.fontMetrics
    // baseline = centerY - (bottom + top)/2
    c.drawText(
        text,
        headerPaddingLeft,
        childView.top - headerHeight / 2 - ((fontMetrics.bottom + fontMetrics.top) / 2f),
        textPaint)
  }

  /**
   * 绘制顶部 常驻、可滚动的header
   */
  private fun drawFakeHeader(
      c: Canvas,
      adapter: IDictionaryAdapter,
      parent: RecyclerView,
      childLayoutPosition: Int,
      scrollY: Int) {
    c.translate(0f, -scrollY.toFloat())
    c.drawRect(
        0f,
        0f,
        parent.width.toFloat(),
        headerHeight.toFloat(),
        headerPaint)
    val text = adapter.getItemPositionHeaderText(childLayoutPosition)
    val fontMetrics = textPaint.fontMetrics
    // baseline = centerY - (bottom + top)/2
    c.drawText(
        text,
        headerPaddingLeft,
        headerHeight / 2 - ((fontMetrics.bottom + fontMetrics.top) / 2f),
        textPaint)
    c.translate(0f, scrollY.toFloat())
  }

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    super.getItemOffsets(outRect, view, parent, state)
    val adapter = parent.adapter as? IDictionaryAdapter ?: return
    val childLayoutPosition = parent.getChildLayoutPosition(view)
    if (adapter.isItemPositionHeader(childLayoutPosition)) {
      outRect.top = headerHeight
    } else {
      outRect.bottom = dividerHeight
    }
  }
}
