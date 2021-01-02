package com.example.canteenchecker.adminapp.proxy;

import android.util.Log;

import com.example.canteenchecker.adminapp.core.Canteen;
import com.example.canteenchecker.adminapp.core.CanteenDetails;
import com.example.canteenchecker.adminapp.core.ReviewData;
import com.google.android.gms.common.internal.IResolveAccountCallbacks;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

// proxy implementation for API version 1.0
// https://moc5.projekte.fh-hagenberg.at/CanteenChecker/swagger/index.html
class ServiceProxyImpl implements ServiceProxy {
  private static final String SERVICE_BASE_URL = "https://moc5.projekte.fh-hagenberg.at/CanteenChecker/api/admin/";

  private final Proxy proxy = new Retrofit.Builder()
          .baseUrl(SERVICE_BASE_URL)
          .addConverterFactory(ScalarsConverterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .build()
          .create(Proxy.class);

  private static String formatAuthToken(String authToken) {
    return String.format("Bearer %s", authToken);
  }

  @Override
  public CanteenDetails getCanteen(String authToken) throws IOException {
    Proxy_CanteenDetails canteen = proxy.getCanteen(String.format("Bearer %s", authToken)).execute().body();
    return canteen != null ? canteen.toCanteenDetails() : null;
  }


  @Override
  public String authenticate(String userName, String password) throws IOException {
    return proxy.postAuthenticate(userName, password).execute().body();
  }

  @Override
  public void updateCanteen(String authToken, String name, String address, String website, String phoneNumber) throws IOException {
    proxy.updateCanteen(String.format("Bearer %s", authToken), name, address, website, phoneNumber).execute();
  }

  @Override
  public void updateCanteenDish(String authToken, String dish, double dishPrice) throws IOException {
    proxy.updateCanteenDish(formatAuthToken(authToken), dish, dishPrice).execute();
  }

  @Override
  public void updateCanteenWaitingTime(String authToken, String waitingTime) throws IOException {
    proxy.updateCanteenWaitingTime(formatAuthToken(authToken), waitingTime).execute();
  }

  @Override
  public Collection<ReviewData> getReviews(String authToken) throws IOException {
    Collection<Proxy_CanteenReview> reviews = proxy.getReviews(formatAuthToken(authToken)).execute().body();
    if (reviews == null) {
      return null;
    }

    Collection<ReviewData> result = new ArrayList<>(reviews.size());
    for (Proxy_CanteenReview review : reviews) {
      result.add(review.toReviewData());
    }
    return result;
  }

  private interface Proxy {
    @POST("authenticate")
    Call<String> postAuthenticate(@Query("userName") String userName, @Query("password") String password);

    @GET("canteen")
    Call<Proxy_CanteenDetails> getCanteen(@Header("Authorization") String authenticationToken);

    @PUT("canteen/data")
    Call<Void> updateCanteen(@Header("Authorization") String authenticationToken,
                                @Query("name") String name,
                                @Query("address") String address,
                                @Query("website") String website,
                                @Query("phoneNumber") String phoneNumber);

    @PUT("canteen/dish")
    Call<Void> updateCanteenDish(@Header("Authorization") String authenticationToken,
                                 @Query("dish") String dish,
                                 @Query("dishPrice") double dishPrice);

    @PUT("canteen/waiting-time")
    Call<Void> updateCanteenWaitingTime(@Header("Authorization") String authenticationToken,
                                        @Query("waitingTime") String waitingTime);

    @GET("canteen/reviews")
    Call<Collection<Proxy_CanteenReview>> getReviews(@Header("Authorization") String authenticationToken);
  }

  private static class Proxy_CanteenData {
    String id;
    String name;
    String dish;
    float dishPrice;
    float averageRating;

    Canteen toCanteen() {
      return new Canteen(id, name, dish, dishPrice, averageRating);
    }
  }

  private static class Proxy_CanteenDetails {
    String name;
    String address;
    String phoneNumber;
    String website;
    String dish;
    float dishPrice;
    int waitingTime;

    CanteenDetails toCanteenDetails() {
      return new CanteenDetails(name, phoneNumber, website, dish, dishPrice, address, waitingTime);
    }
  }

  private static class Proxy_CanteenReview {
    String id;
    String creationDate;
    String creator;
    int rating;
    String remark;

    ReviewData toReviewData() {
      return new ReviewData(rating, remark, id, creator, creationDate);
    }
  }
}
