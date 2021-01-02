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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
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
  private Button btnReviews;
  private EditText edtName;
  private EditText edtMenu;
  private EditText edtPrice;
  private EditText edtWaitingTime;
  private EditText edtWeb;
  private EditText edtPhone;
  private EditText edtAddress;
  private SupportMapFragment mpfAddress;

  public static Intent createIntent(Context context) {
    return new Intent(context, CanteenDetailActivity.class);
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
      googleMap.setOnMapLongClickListener(latLng -> {
        handleMapLongClick(googleMap, latLng);
      });
    });

    setUiEditable(uiEditable);

    btnEdit.setOnClickListener(v -> {
      uiEditable = !uiEditable;
      setUiEditable(uiEditable);
      if (!uiEditable) {
        updateCanteenDetails();
      }
    });

    btnSave.setOnClickListener(v -> {
      uiEditable = false;
      saveCanteen();
      setUiEditable(uiEditable);
    });

    btnCancel.setOnClickListener(v -> {
      uiEditable = false;
      updateCanteenDetails();
      setUiEditable(uiEditable);
    });

    btnReviews.setOnClickListener(v -> {
      v.getContext().startActivity(ReviewListActivity.createIntent(v.getContext()));
    });



    updateCanteenDetails();
  }

  private void handleMapLongClick(GoogleMap googleMap, LatLng latLng) {
    if (!uiEditable) {
      return;
    }

    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(latLng);

    googleMap.clear();

    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

    googleMap.addMarker(markerOptions);


    Geocoder geocoder = new Geocoder(CanteenDetailActivity.this);
    try {
      Address addr = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
      edtAddress.setText(addr.getAddressLine(0));
      Log.e(TAG, String.format("New Address: %s", addr.getAddressLine(0)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void saveCanteen() {
    try {
      saveCanteenData();
      saveCanteenDish();
      saveCanteenWaitingTime();
    } catch (ParseException e) {
      Log.e(TAG, String.format("Parsing values during update failed", e));
    }
  }

  @SuppressLint("StaticFieldLeak")
  private void saveCanteenData() {
    String token = ((CanteenAdminApplication) getApplication()).getAuthenticationToken();
    new AsyncTask<String, Void, Void>() {
      @Override
      protected Void doInBackground(String... strings) {
        try {
          ServiceProxyFactory.createProxy().updateCanteen(token, strings[0], strings[3], strings[1], strings[2]);
        } catch (IOException e) {
          Log.e(TAG, String.format("Saving Canteen-data failed", e));
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        updateCanteenDetails();
        Toast.makeText(
                CanteenDetailActivity.this,
                R.string.message_update_done,
                Toast.LENGTH_SHORT)
                .show();
      }
    }.execute(edtName.getText().toString(),
            edtWeb.getText().toString(),
            edtPhone.getText().toString(),
            edtAddress.getText().toString());
  }

  @SuppressLint("StaticFieldLeak")
  private void saveCanteenDish() throws ParseException {
    double dishPrice = NumberFormat.getCurrencyInstance()
            .parse(edtPrice.getText().toString()).doubleValue();

    String token = ((CanteenAdminApplication) getApplication()).getAuthenticationToken();
    new AsyncTask<Object, Void, Void>() {
      @Override
      protected Void doInBackground(Object... objects) {
        try {
          ServiceProxyFactory.createProxy().updateCanteenDish(token, (String) objects[0], (double) objects[1]);
        } catch (IOException e) {
          Log.e(TAG, String.format("Saving dish-data failed", e));
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        updateCanteenDetails();
        Toast.makeText(
                CanteenDetailActivity.this,
                R.string.message_update_done,
                Toast.LENGTH_SHORT)
                .show();
      }
    }.execute(edtMenu.getText().toString(), dishPrice);
  }

  @SuppressLint("StaticFieldLeak")
  private void saveCanteenWaitingTime() {
    String token = ((CanteenAdminApplication) getApplication()).getAuthenticationToken();
    new AsyncTask<String, Void, Void>() {
      @Override
      protected Void doInBackground(String... strings) {
        try {
          ServiceProxyFactory.createProxy().updateCanteenWaitingTime(token, strings[0]);
        } catch (IOException e) {
          Log.e(TAG, String.format("Saving dish-data failed", e));
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        updateCanteenDetails();
        Toast.makeText(
                CanteenDetailActivity.this,
                R.string.message_update_done,
                Toast.LENGTH_SHORT)
                .show();
      }
    }.execute(edtWaitingTime.getText().toString());
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
    btnReviews = findViewById(R.id.btnReviews);
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

    mpfAddress.getMapAsync(googleMap -> {
      UiSettings uiSettings = googleMap.getUiSettings();
      uiSettings.setAllGesturesEnabled(value);
    });
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

        Log.e(TAG, String.format("Show"));
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
