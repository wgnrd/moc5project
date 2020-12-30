package com.example.canteenchecker.adminapp.proxy;

import com.example.canteenchecker.adminapp.core.Canteen;
import com.example.canteenchecker.adminapp.core.CanteenDetails;
import com.example.canteenchecker.adminapp.core.ReviewData;

import java.io.IOException;
import java.util.Collection;

public interface ServiceProxy {
    Collection<Canteen> getCanteens(String filter) throws IOException;

    CanteenDetails getCanteen(String authToken) throws IOException;

    ReviewData getReviewsDataForCanteen(String canteenId) throws IOException;

    String authenticate(String userName, String password) throws IOException;

    void createReview(String authToken, String canteenId, int rating, String remark) throws IOException;

    void updateCanteen(String authToken, String name, String address, String website, String phoneNumber) throws IOException;

    void updateCanteenDish(String authToken, String dish, double dishPrice) throws IOException;
}
