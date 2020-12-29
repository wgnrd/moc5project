package com.example.canteenchecker.adminapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.canteenchecker.adminapp.CanteenAdminApplication;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.adminapp.core.CanteenDetails;
import com.example.canteenchecker.adminapp.proxy.ServiceProxyFactory;

import java.io.IOException;
import java.text.NumberFormat;

public class CanteenDetailActivity extends AppCompatActivity {
  private static final String TAG = CanteenDetailActivity.class.toString();
  private static final String CANTEEN_ID_KEY = "CanteenId";

  private CanteenDetails canteen = null;

  private EditText edtName;
  private EditText edtMenu;
  private EditText edtPrice;
  private EditText edtWaitingTime;
  private EditText edtWeb;
  private EditText edtPhone;

  public static Intent createIntent(Context context, String canteenId) {
    Intent intent = new Intent(context, CanteenDetailActivity.class);
    intent.putExtra(CANTEEN_ID_KEY, canteenId);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_canteen_details);

    edtName = findViewById(R.id.edtName);
    edtMenu = findViewById(R.id.edtMenu);
    edtPrice = findViewById(R.id.edtPrice);
    edtWaitingTime = findViewById(R.id.edtWaitingTime);
    edtWeb = findViewById(R.id.edtWeb);
    edtPhone = findViewById(R.id.edtPhone);

    updateCanteenDetails();
  }

  @SuppressLint("StaticFieldLeak")
  private void updateCanteenDetails() {
    String token = ((CanteenAdminApplication) getApplication()).getAuthenticationToken();

    new AsyncTask<String, Void, CanteenDetails>() {
      @Override
      protected CanteenDetails doInBackground(String... strings) {
        try {
          CanteenDetails canteen = ServiceProxyFactory.createProxy().getCanteen(token);
          Log.e(TAG, String.format("Canteen '%s'.", canteen.getName()));
          return canteen;
        } catch (IOException e) {
          Toast.makeText(
                CanteenDetailActivity.this,
                R.string.message_canteen_not_found,
                Toast.LENGTH_SHORT)
                .show();
          finish();
          return null;
        }
      }

      @Override
      protected void onPostExecute(CanteenDetails canteenDetails) {
        super.onPostExecute(canteenDetails);
        canteen = canteenDetails;

        if (canteen == null) {
          Toast.makeText(
                  CanteenDetailActivity.this,
                  R.string.message_canteen_not_found,
                  Toast.LENGTH_SHORT)
                  .show();
          finish();
          return;
        }

        edtName.setText(canteen.getName());
        edtMenu.setText(canteen.getDish());
        edtPrice.setText(NumberFormat.getCurrencyInstance().format(canteen.getDishPrice()));
        edtWaitingTime.setText(String.format("%s", canteen.getWaitingTime()));
        edtPhone.setText(canteen.getPhoneNumber());
        edtWeb.setText(canteen.getWebsite());

      }
    }.execute(token);
  }
}
