package com.example.canteenchecker.adminapp.proxy;

import com.example.canteenchecker.adminapp.core.Canteen;
import com.example.canteenchecker.adminapp.core.CanteenDetails;
import com.example.canteenchecker.adminapp.core.ReviewData;
import com.example.canteenchecker.adminapp.core.ReviewStatisticData;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;

import java.io.IOException;
import java.util.Collection;

public interface ServiceProxy {
    CanteenDetails getCanteen(String authToken) throws IOException;

    String authenticate(String userName, String password) throws IOException;

  void updateCanteen(String authToken, String name, String address, String website, String phoneNumber) throws IOException;

  void updateCanteenDish(String authToken, String dish, double dishPrice) throws IOException;

  void updateCanteenWaitingTime(String authToken, String waitingTime) throws IOException;

  Collection<ReviewData> getReviews(String authToken) throws IOException;

  void deleteReview(String authToken, String reviewId) throws IOException;

  ReviewStatisticData getReviewStatistics(String authToken) throws IOException;
}
