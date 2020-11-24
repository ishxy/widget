package com.imuguys.widget;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.imuguys.widget.databinding.ActivityMainBinding;
import com.imuguys.widget.shxy.dictionary.SimpleDictionaryAdapter;
import com.imuguys.widget.shxy.dictionary.DictionaryItemDecoration;
import com.imuguys.widget.shxy.dictionary.SimpleDictionaryData;

public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding mActivityMainBinding;
  private List<SimpleDictionaryData> mSimpleDictionaryData = new ArrayList<>();
  private SimpleDictionaryAdapter mSimpleDictionaryAdapter = new SimpleDictionaryAdapter();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mActivityMainBinding =
        DataBindingUtil.setContentView(this, R.layout.activity_main);
    dictionaryTest();
  }

  private void dictionaryTest() {
    mActivityMainBinding.dictionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mSimpleDictionaryAdapter.setDictionaryDataList(mSimpleDictionaryData);
    for (int i = 0; i < 26; i++) {
      mSimpleDictionaryData.add(new SimpleDictionaryData((char) ('a' + i) + "", true));
      mSimpleDictionaryData.add(new SimpleDictionaryData((char) ('a' + i) + "", false));
      mSimpleDictionaryData.add(new SimpleDictionaryData((char) ('a' + i) + "", false));
    }
    mActivityMainBinding.dictionRecyclerView.setAdapter(mSimpleDictionaryAdapter);
    mActivityMainBinding.dictionRecyclerView.addItemDecoration(new DictionaryItemDecoration());
    mSimpleDictionaryAdapter.notifyDataSetChanged();
  }
}