package com.example.canteenchecker.adminapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.canteenchecker.adminapp.CanteenAdminApplication;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.adminapp.core.ReviewData;
import com.example.canteenchecker.adminapp.proxy.ServiceProxy;
import com.example.canteenchecker.adminapp.proxy.ServiceProxyFactory;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ReviewListActivity  extends AppCompatActivity {
  private static final String TAG = ReviewListActivity.class.toString();

  public static Intent createIntent(Context context) {
    return new Intent(context, ReviewListActivity.class);
  }

  private final ReviewsAdapter reviewsAdapter = new ReviewsAdapter();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_review_list);

    RecyclerView rcvReviews = findViewById(R.id.rcvReviews);
    rcvReviews.setLayoutManager(new LinearLayoutManager(this));
    rcvReviews.setAdapter(reviewsAdapter);

    updateReviews();
  }

  @SuppressLint("StaticFieldLeak")
  private void updateReviews() {
    String token = ((CanteenAdminApplication) getApplication()).getAuthenticationToken();
    new AsyncTask<String, Void, Collection<ReviewData>>() {
      @Override
      protected Collection<ReviewData> doInBackground(String... strings) {
        try {
          return ServiceProxyFactory.createProxy().getReviews(token);
        } catch (IOException e) {
          Log.e(TAG, String.format("Loading reviews failed ", e));
          return null;
        }
      }

      @Override
      protected void onPostExecute(Collection<ReviewData> reviewData) {
        reviewsAdapter.displayReviews(reviewData);
        Log.i(TAG, "Reviews loaded");
      }
    }.execute(token);
  }


  private static class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private final List<ReviewData> reviewDataList = new ArrayList<>();

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
    }

    @Override
    public int getItemCount() {
      return reviewDataList.size();
    }

    void displayReviews(Collection<ReviewData> reviewData) {
      reviewDataList.clear();
      Log.e(TAG, String.format("Hello, %s", reviewData.size()));
      if (reviewData != null) {
        reviewDataList.addAll(reviewData);
      }
      notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
      private TextView txvRemark = itemView.findViewById(R.id.txvRemark);
      private TextView txvCreator = itemView.findViewById(R.id.txvCreator);
      private TextView txvCreationDate = itemView.findViewById(R.id.txvCreationDate);
      private RatingBar rtbRating = itemView.findViewById(R.id.rtbRating);
      private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

      public ViewHolder(@NonNull View itemView) {
        super(itemView);
      }

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