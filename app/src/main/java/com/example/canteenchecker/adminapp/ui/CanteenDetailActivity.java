package com.example.canteenchecker.adminapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.canteenchecker.adminapp.R;

public class CanteenDetailActivity extends AppCompatActivity {
  private static final String TAG = CanteenDetailActivity.class.toString();
  private static final String CANTEEN_ID_KEY = "CanteenId";

  public static Intent createIntent(Context context, String canteenId) {
    Intent intent = new Intent(context, CanteenDetailActivity.class);
    intent.putExtra(CANTEEN_ID_KEY, canteenId);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_canteen_details);
  }
}
