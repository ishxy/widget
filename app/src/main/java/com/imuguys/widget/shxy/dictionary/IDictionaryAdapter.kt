package com.imuguys.widget.shxy.dictionary

interface IDictionaryAdapter {
  fun isItemPositionHeader(position : Int) : Boolean
  fun getItemPositionHeaderText(position : Int) : String
}