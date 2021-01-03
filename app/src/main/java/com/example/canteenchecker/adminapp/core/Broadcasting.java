package com.example.canteenchecker.adminapp.core;

import android.content.Intent;
import android.content.IntentFilter;

public class Broadcasting {
  private static final String REVIEW_CHANGED_INTENT_ACTION = "ReviewChanged";

  private Broadcasting() {

  }

  public static IntentFilter createReviewsChangedBroadcastIntentFilter() {
    return new IntentFilter(REVIEW_CHANGED_INTENT_ACTION);
  }

  public static Intent createReviewsChangedBroadcastIntent() {
    return new Intent(REVIEW_CHANGED_INTENT_ACTION);
  }
}
