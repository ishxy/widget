package com.imuguys.widget.shxy.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.imuguys.widget.R

class SimpleDictionaryAdapter : RecyclerView.Adapter<DictionaryViewHolder>(), IDictionaryAdapter {
  var dictionaryDataList = emptyList<SimpleDictionaryData>()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.simple_text_view_match_parent, parent, false)
    return DictionaryViewHolder(view)
  }

  override fun onBindViewHolder(holder: DictionaryViewHolder, position: Int) {
    holder.textView.text = dictionaryDataList[position].text
  }

  override fun getItemCount(): Int {
    return dictionaryDataList.size
  }

  override fun isItemPositionHeader(position: Int): Boolean {
    return if (position !in dictionaryDataList.indices) false
    else dictionaryDataList[position].isHeader()
  }

  override fun getItemPositionHeaderText(position: Int): String {
    return if (position !in dictionaryDataList.indices) ""
    else "第${dictionaryDataList[position].text ?: "x"}组"
  }
}

class DictionaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
  val textView: TextView = itemView.findViewById(R.id.text_view_1)
}