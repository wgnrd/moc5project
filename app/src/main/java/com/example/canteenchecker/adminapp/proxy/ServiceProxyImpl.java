package com.example.canteenchecker.adminapp.proxy;

import com.example.canteenchecker.adminapp.core.Canteen;
import com.example.canteenchecker.adminapp.core.CanteenDetails;
import com.example.canteenchecker.adminapp.core.ReviewData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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


  @Override
  public Collection<Canteen> getCanteens(String filter) throws IOException {
    Collection<Proxy_CanteenData> canteens = proxy.getCanteens(filter).execute().body();
    if (canteens == null) {
      return null;
    }
    Collection<Canteen> result = new ArrayList<>(canteens.size());
    for (Proxy_CanteenData canteen : canteens) {
      result.add(canteen.toCanteen());
    }
    return result;
  }

  @Override
  public CanteenDetails getCanteen(String authToken) throws IOException {
    Proxy_CanteenDetails canteen = proxy.getCanteen(String.format("Bearer %s", authToken)).execute().body();
    return canteen != null ? canteen.toCanteenDetails() : null;
  }

  @Override
  public ReviewData getReviewsDataForCanteen(String canteenId) throws IOException {
    Proxy_CanteenReviewStatistics reviewData = proxy.getReviewStatisticsForCanteen(canteenId).execute().body();
    return reviewData != null ? reviewData.toReviewData() : null;
  }

  @Override
  public String authenticate(String userName, String password) throws IOException {
    return proxy.postAuthenticate(userName, password).execute().body();
  }

  @Override
  public void createReview(String authToken, String canteenId, int rating, String remark) throws IOException {
    // TODO make bearer token nicer
    proxy.postCanteenReview(String.format("Bearer %s", authToken), canteenId, rating, remark).execute();
  }

  private interface Proxy {
    @POST("authenticate")
    Call<String> postAuthenticate(@Query("userName") String userName, @Query("password") String password);

    @GET("canteens")
    Call<Collection<Proxy_CanteenData>> getCanteens(@Query("name") String name);

    @GET("canteen")
    Call<Proxy_CanteenDetails> getCanteen(@Header("Authorization") String authenticationToken);

    @GET("canteens/{canteenId}/review-statistics")
    Call<Proxy_CanteenReviewStatistics> getReviewStatisticsForCanteen(@Path("canteenId") String canteenId);

    @POST("canteens/{canteenId}/reviews")
    Call<Void> postCanteenReview(@Header("Authorization") String authenticationToken,
                                 @Path("canteenId") String canteenId,
                                 @Query("rating") int rating,
                                 @Query("remark") String remark);
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

  private static class Proxy_CanteenReviewStatistics {
    int countOneStar;
    int countTwoStars;
    int countThreeStars;
    int countFourStars;
    int countFiveStars;

    ReviewData toReviewData() {
      return new ReviewData(countOneStar, countTwoStars, countThreeStars, countFourStars, countFiveStars);
    }
  }
}
