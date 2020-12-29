package com.example.canteenchecker.adminapp;

import android.app.Application;

public class CanteenAdminApplication extends Application {
  private String authenticationToken = null;

  @Override
  public void onCreate() {
    super.onCreate();
    // MyFireBaseMessagingService.subscribeToCanteenUpdates();
  }

  public synchronized void setAuthenticationToken(String authenticationToken) { this.authenticationToken = authenticationToken; }
  public synchronized String getAuthenticationToken() { return authenticationToken; }
  public synchronized boolean isAuthenticated() { return getAuthenticationToken() != null; }
}
