package com.example.canteenchecker.adminapp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.canteenchecker.adminapp.CanteenAdminApplication;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.adminapp.core.Broadcasting;
import com.example.canteenchecker.adminapp.core.ReviewData;
import com.example.canteenchecker.adminapp.proxy.ServiceProxyFactory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReviewListActivity extends AppCompatActivity {
  private static final String TAG = ReviewListActivity.class.toString();

  public static Intent createIntent(Context context) {
    return new Intent(context, ReviewListActivity.class);
  }

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      updateReviews();
    }
  };

  private final ReviewsAdapter reviewsAdapter = new ReviewsAdapter(this);
  private SwipeRefreshLayout srlSwipeRefreshLayout;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_review_list);

    RecyclerView rcvReviews = findViewById(R.id.rcvReviews);
    rcvReviews.setLayoutManager(new LinearLayoutManager(this));
    rcvReviews.setAdapter(reviewsAdapter);

    srlSwipeRefreshLayout = findViewById(R.id.srlSwipeRefreshLayout);
    srlSwipeRefreshLayout.setOnRefreshListener(this::updateReviews);

    getFragmentManager().beginTransaction()
            .replace(R.id.lnlReviewStatistic, ReviewStatisticFragment.create()).commit();


    LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver,
                    Broadcasting.createReviewsChangedBroadcastIntentFilter());

    updateReviews();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
  }

  @SuppressLint("StaticFieldLeak")
  private void updateReviews() {
    srlSwipeRefreshLayout.setRefreshing(true);
    String token = ((CanteenAdminApplication) getApplication()).getAuthenticationToken();
    new AsyncTask<String, Void, Collection<ReviewData>>() {
      @Override
      protected Collection<ReviewData> doInBackground(String... strings) {
        try {
          return ServiceProxyFactory.createProxy().getReviews(token);
        } catch (IOException e) {
          Log.e(TAG, String.format("Loading reviews failed %s", e));
          return null;
        }
      }

      @Override
      protected void onPostExecute(Collection<ReviewData> reviewData) {
        reviewsAdapter.displayReviews(reviewData);
        Log.i(TAG, "Reviews loaded");
        srlSwipeRefreshLayout.setRefreshing(false);
      }
    }.execute(token);
  }

  @SuppressLint("StaticFieldLeak")
  void DeleteReview(String reviewId) {
    final View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_review, null);

    String token = ((CanteenAdminApplication) getApplication()).getAuthenticationToken();

    new AlertDialog.Builder(this)
            .setTitle(R.string.dialog_delete_review)
            .setView(view)
            .setPositiveButton(R.string.text_yes, (dialog, which) -> {
              dialog.dismiss();
              new AsyncTask<String, Void, Void>() {
                @Override
                protected Void doInBackground(String... strings) {
                  try {
                    ServiceProxyFactory.createProxy().deleteReview(token, reviewId);
                  } catch (IOException e) {
                    Log.e(TAG, "Something went wrong during removal of reviews");
                  }
                  return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                  updateReviews();
                }
              }.execute(token);
            }).create().show();
  }


  private static class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private final List<ReviewData> reviewDataList = new ArrayList<>();
    private final ReviewListActivity reviewListActivity;

    public ReviewsAdapter(ReviewListActivity reviewListActivity) {
      this.reviewListActivity = reviewListActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.item_review, parent, false);

      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.updateView(reviewDataList.get(position));
      holder.btnDelete.setOnClickListener(v -> reviewListActivity
              .DeleteReview(reviewDataList.get(position).getId()));
    }

    @Override
    public int getItemCount() {
      return reviewDataList.size();
    }

    void displayReviews(Collection<ReviewData> reviewData) {
      reviewDataList.clear();
      if (reviewData != null) {
        reviewDataList.addAll(reviewData);
      }
      notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
      private final TextView txvRemark = itemView.findViewById(R.id.txvRemark);
      private final TextView txvCreator = itemView.findViewById(R.id.txvCreator);
      private final TextView txvCreationDate = itemView.findViewById(R.id.txvCreationDate);
      private final RatingBar rtbRating = itemView.findViewById(R.id.rtbRating);
      private final Button btnDelete = itemView.findViewById(R.id.btnDelete);
      private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

      public ViewHolder(@NonNull View itemView) {
        super(itemView);
      }

      @SuppressLint("StaticFieldLeak")
      void updateView(final ReviewData review) {
        txvRemark.setText(review.getRemark());
        txvCreator.setText(review.getCreator());
        txvCreationDate.setText(dateFormatter.format(review.getCreationDate()));
        rtbRating.setRating(review.getRating());
      }
    }
  }
}
//496359e5-0cf3-4ab9-b028-ef2313c58100