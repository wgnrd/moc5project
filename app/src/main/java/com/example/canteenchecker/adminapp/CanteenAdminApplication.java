package com.example.canteenchecker.adminapp;

import android.app.Application;

import com.example.canteenchecker.adminapp.service.MyFireBaseMessagingService;
import com.google.firebase.FirebaseApp;

public class CanteenAdminApplication extends Application {
  private String authenticationToken = null;

  @Override
  public void onCreate() {
    super.onCreate();
    MyFireBaseMessagingService.subscribeToReviewUpdates();
  }

  public synchronized void setAuthenticationToken(String authenticationToken) { this.authenticationToken = authenticationToken; }
  public synchronized String getAuthenticationToken() { return authenticationToken; }
  public synchronized boolean isAuthenticated() { return getAuthenticationToken() != null; }
}
