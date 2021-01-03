package com.example.canteenchecker.adminapp.service;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.canteenchecker.adminapp.core.Broadcasting;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.annotation.Target;


public class MyFireBaseMessagingService extends FirebaseMessagingService {
  private static final String REMOTE_MESSAGE_TOPIC = "CanteenUpdates";
  private static final String TAG = MyFireBaseMessagingService.class.toString();

  public static void subscribeToReviewUpdates() {
    FirebaseMessaging.getInstance().subscribeToTopic(REMOTE_MESSAGE_TOPIC)
            .addOnSuccessListener(command -> Log.d(TAG, "Subscription was successful"));
  }

  @Override
  public void onNewToken(@NonNull String s) {
    subscribeToReviewUpdates();
  }

  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    Log.d(TAG, "From : " + remoteMessage.getFrom());
    LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Broadcasting.createReviewsChangedBroadcastIntent());

    super.onMessageReceived(remoteMessage);
  }
}
