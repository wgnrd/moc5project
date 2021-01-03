package com.example.canteenchecker.adminapp.service;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.canteenchecker.adminapp.core.Broadcasting;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFireBaseMessagingService extends FirebaseMessagingService {
  private static final String REMOTE_MESSAGE_TOPIC = "ReviewUpdates";

  public static void subscribeToReviewUpdates() {
    FirebaseMessaging.getInstance().subscribeToTopic(REMOTE_MESSAGE_TOPIC);
  }

  @Override
  public void onNewToken(@NonNull String s) {
    subscribeToReviewUpdates();
  }

  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Broadcasting.createReviewsChangedBroadcastIntent());

    super.onMessageReceived(remoteMessage);
  }
}
