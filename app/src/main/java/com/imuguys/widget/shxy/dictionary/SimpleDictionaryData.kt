package com.imuguys.widget.shxy.dictionary

class SimpleDictionaryData(var text: String?, private var header: Boolean) : DictionaryData {
  override fun isHeader(): Boolean {
    return header
  }
}