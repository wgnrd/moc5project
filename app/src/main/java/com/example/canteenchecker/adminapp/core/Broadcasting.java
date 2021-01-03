package com.example.canteenchecker.adminapp.core;

import android.content.Intent;
import android.content.IntentFilter;

public class Broadcasting {
  private static final String CANTEEN_CHANGED_INTENT_ACTION = "CanteenChanged";
  private static final String REVIEW_CHANGED_INTENT_ACTION = "CanteenChanged";
  private static final String CANTEEN_CHANGED_INTENT_CANTEEN_ID = "CanteenId";

  private Broadcasting() {

  }

  public static Intent createCanteenChangedBroadcastIntent(String canteenId) {
    Intent intent = new Intent(CANTEEN_CHANGED_INTENT_ACTION);
    intent.putExtra(CANTEEN_CHANGED_INTENT_CANTEEN_ID, canteenId);
    return intent;
  }

  public static String extractCanteenId(Intent intent) {
    return intent.getStringExtra(CANTEEN_CHANGED_INTENT_CANTEEN_ID);
  }

  public static IntentFilter createCanteenChangedBroadcastIntentFilter() {
    return new IntentFilter(CANTEEN_CHANGED_INTENT_ACTION);
  }

  public static IntentFilter createReviewsChangedBroadcastIntentFilter() {
    return new IntentFilter(REVIEW_CHANGED_INTENT_ACTION);
  }

  public static Intent createReviewsChangedBroadcastIntent() {
    return new Intent();
  }
}
