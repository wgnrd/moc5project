package com.example.canteenchecker.adminapp.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.canteenchecker.adminapp.CanteenAdminApplication;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.adminapp.core.Broadcasting;
import com.example.canteenchecker.adminapp.core.ReviewStatisticData;
import com.example.canteenchecker.adminapp.proxy.ServiceProxyFactory;

import java.io.IOException;
import java.text.NumberFormat;

public class ReviewStatisticFragment extends Fragment {
  private static final String TAG = ReviewStatisticFragment.class.toString();

  public static Fragment create() {
    ReviewStatisticFragment reviewStatisticFragment = new ReviewStatisticFragment();
    Bundle arguments = new Bundle();
    return reviewStatisticFragment;
  }

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      updateReviewStatistics();
    }
  };

  private TextView txvAverageRating;
  private RatingBar rtbAverageRating;
  private TextView txvTotalRatings;
  private ProgressBar prbRatingsOne;
  private ProgressBar prbRatingsTwo;
  private ProgressBar prbRatingsThree;
  private ProgressBar prbRatingsFour;
  private ProgressBar prbRatingsFive;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_review_statistic, container, false);
    txvAverageRating = view.findViewById(R.id.txvAverageRating);
    rtbAverageRating = view.findViewById(R.id.rtbAverageRating);
    txvTotalRatings = view.findViewById(R.id.txvTotalRatings);
    prbRatingsOne = view.findViewById(R.id.prbRatingsOne);
    prbRatingsTwo = view.findViewById(R.id.prbRatingsTwo);
    prbRatingsThree = view.findViewById(R.id.prbRatingsThree);
    prbRatingsFour = view.findViewById(R.id.prbRatingsFour);
    prbRatingsFive = view.findViewById(R.id.prbRatingsFive);

    LocalBroadcastManager.getInstance(getActivity())
            .registerReceiver(broadcastReceiver, Broadcasting.createCanteenChangedBroadcastIntentFilter());

    updateReviewStatistics();

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver); // ! ! !
  }

  @SuppressWarnings("StaticFieldLeak")
  private void updateReviewStatistics() {
    Log.d(TAG, "updateReviewStatistics");
    String token = ((CanteenAdminApplication) getActivity().getApplication()).getAuthenticationToken();
    new AsyncTask<String, Void, ReviewStatisticData>() {

      @Override
      protected ReviewStatisticData doInBackground(String... strings) {
        try {
          return ServiceProxyFactory.createProxy().getReviewStatistics(token);
        } catch (IOException e) {
          Log.e(TAG, String.format("Download of reviews for canteen with id '%s' failed.", strings[0]), e);
          return null;
        }
      }

      @Override
      protected void onPostExecute(ReviewStatisticData reviewData) {
        if (reviewData != null) {
          txvAverageRating.setText(NumberFormat.getNumberInstance().format(reviewData.getAverageRating()));
          rtbAverageRating.setRating(reviewData.getAverageRating());
          txvTotalRatings.setText(NumberFormat.getNumberInstance().format(reviewData.getTotalRatings()));

          prbRatingsOne.setMax(reviewData.getTotalRatings());
          prbRatingsOne.setProgress(reviewData.getRatingsOne());

          prbRatingsTwo.setMax(reviewData.getTotalRatings());
          prbRatingsTwo.setProgress(reviewData.getRatingsTwo());

          prbRatingsThree.setMax(reviewData.getTotalRatings());
          prbRatingsThree.setProgress(reviewData.getRatingsThree());

          prbRatingsFour.setMax(reviewData.getTotalRatings());
          prbRatingsFour.setProgress(reviewData.getRatingsFour());

          prbRatingsFive.setMax(reviewData.getTotalRatings());
          prbRatingsFive.setProgress(reviewData.getRatingsFive());
        } else {
          txvAverageRating.setText(null);
          rtbAverageRating.setRating(0);
          txvTotalRatings.setText(null);

          prbRatingsOne.setMax(1);
          prbRatingsOne.setProgress(0);

          prbRatingsTwo.setMax(1);
          prbRatingsTwo.setProgress(0);

          prbRatingsThree.setMax(1);
          prbRatingsThree.setProgress(0);

          prbRatingsFour.setMax(1);
          prbRatingsFour.setProgress(0);

          prbRatingsFive.setMax(1);
          prbRatingsFive.setProgress(0);
        }
      }
    }.execute(token);
  }
}
