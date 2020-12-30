package com.example.canteenchecker.adminapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.canteenchecker.adminapp.CanteenAdminApplication;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.adminapp.core.CanteenDetails;
import com.example.canteenchecker.adminapp.proxy.ServiceProxyFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

public class CanteenDetailActivity extends AppCompatActivity {
  private static final String TAG = CanteenDetailActivity.class.toString();
  private static final String CANTEEN_ID_KEY = "CanteenId";
  private static final float DEFAULT_MAP_ZOOM_FACTOR = 15;
  private boolean uiEditable = false;

  private CanteenDetails canteen = null;

  private Button btnSave;
  private Button btnCancel;
  private Button btnEdit;
  private EditText edtName;
  private EditText edtMenu;
  private EditText edtPrice;
  private EditText edtWaitingTime;
  private EditText edtWeb;
  private EditText edtPhone;
  private EditText edtAddress;
  private SupportMapFragment mpfAddress;

  public static Intent createIntent(Context context, String canteenId) {
    Intent intent = new Intent(context, CanteenDetailActivity.class);
    intent.putExtra(CANTEEN_ID_KEY, canteenId);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_canteen_details);

    bindUiElements();

    mpfAddress.getMapAsync(googleMap -> {
      UiSettings uiSettings = googleMap.getUiSettings();
      uiSettings.setAllGesturesEnabled(false);
      uiSettings.setZoomControlsEnabled(true);
    });

    setUiEditable(uiEditable);

    btnEdit.setOnClickListener(v -> {
      uiEditable = !uiEditable;
      setUiEditable(uiEditable);
    });

    btnSave.setOnClickListener(v -> {
      uiEditable = !uiEditable;
      saveCanteen();
      setUiEditable(uiEditable);
    });

    updateCanteenDetails();
  }

  private void saveCanteen() {

  }

  private void bindUiElements() {
    btnEdit = findViewById(R.id.btnEdit);
    btnCancel = findViewById(R.id.btnCancel);
    btnSave = findViewById(R.id.btnSave);
    edtName = findViewById(R.id.edtName);
    edtMenu = findViewById(R.id.edtMenu);
    edtPrice = findViewById(R.id.edtPrice);
    edtWaitingTime = findViewById(R.id.edtWaitingTime);
    edtWeb = findViewById(R.id.edtWeb);
    edtPhone = findViewById(R.id.edtPhone);
    edtAddress = findViewById(R.id.edtAddress);
    mpfAddress = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.mpfMap);
  }

  private void setUiEditable(boolean value) {
    btnCancel.setEnabled(value);
    btnSave.setEnabled(value);

    edtName.setEnabled(value);
    edtMenu.setEnabled(value);
    edtPrice.setEnabled(value);
    edtWaitingTime.setEnabled(value);
    edtWeb.setEnabled(value);
    edtPhone.setEnabled(value);
    edtAddress.setEnabled(value);


  }

  @SuppressLint("StaticFieldLeak")
  private void updateCanteenDetails() {
    String token = ((CanteenAdminApplication) getApplication()).getAuthenticationToken();

    new AsyncTask<String, Void, CanteenDetails>() {
      @Override
      protected CanteenDetails doInBackground(String... strings) {
        try {
          CanteenDetails canteen = ServiceProxyFactory.createProxy().getCanteen(token);
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
        edtAddress.setText(canteen.getLocation());

        Log.w(TAG, String.format("***************************************************"));
        updateMapFragment(canteenDetails);
      }

      private void updateMapFragment(CanteenDetails canteenDetails) {
        new AsyncTask<String, Void, LatLng>() {
          @Override
          protected LatLng doInBackground(String... strings) {
            LatLng location = null;
            Geocoder geocoder = new Geocoder(CanteenDetailActivity.this);
            try {
              List<Address> addresses = strings[0] == null ? null : geocoder.getFromLocationName(strings[0], 1);
              if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                location = new LatLng(address.getLatitude(), address.getLongitude());
              } else {
                Log.w(TAG, String.format("No locations found for '%s'", strings[0]));
              }
            } catch (IOException e) {
              Log.w(TAG, String.format("Locations lookup for '%s' failed", strings[0]));
            }
            return location;
          }

          @Override
          protected void onPostExecute(final LatLng latLng) {
            mpfAddress.getMapAsync(googleMap -> {
              googleMap.clear();
              Log.w(TAG, String.format("Location", latLng));

              if (latLng != null) {
                googleMap.addMarker(new MarkerOptions().position(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM_FACTOR));
              } else {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0,0), 0));
              }
            });
          }
        }.execute(canteenDetails.getLocation());
      }
    }.execute(token);
  }
}
