package com.example.canteenchecker.adminapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.canteenchecker.adminapp.CanteenAdminApplication;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.adminapp.core.Canteen;
import com.example.canteenchecker.adminapp.proxy.ServiceProxyFactory;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
  private static final String TAG = LoginActivity.class.toString();

  private EditText edtUserName;
  private EditText edtPassword;
  private Button btnLogIn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    edtUserName = findViewById(R.id.edtUserName);
    edtPassword = findViewById(R.id.edtPassword);
    btnLogIn = findViewById(R.id.btnLogIn);


    if (((CanteenAdminApplication) getApplication()).isAuthenticated()) {
      // TODO - maybe check if authenticated and throw back to details view
    }
    btnLogIn.setOnClickListener(this::LoginHandler);
  }


  private void setUIEnabled(boolean enabled) {
    btnLogIn.setEnabled(enabled);
    edtUserName.setEnabled(enabled);
    edtPassword.setEnabled(enabled);
  }

  @SuppressLint("StaticFieldLeak")
  private void LoginHandler(View v) {
    setUIEnabled(false);
    new AsyncTask<String, Void, String>() {
      @Override
      protected String doInBackground(String... strings) {
        try {
        return ServiceProxyFactory.createProxy().authenticate(strings[0], strings[1]);
      } catch (IOException e) {
        Log.e(TAG, String.format("Login failed for user name '%s'.", strings[0]), e);
        return null;
      }
      }

      @Override
      protected void onPostExecute(String s) {
        if (s != null) {
        ((CanteenAdminApplication) getApplication()).setAuthenticationToken(s);
        setResult(RESULT_OK);
        v.getContext().startActivity(
                CanteenDetailActivity.createIntent(v.getContext(), "1"));
      } else {
        setUIEnabled(true);
        edtPassword.setText(null);
        Toast.makeText(
                LoginActivity.this,
                R.string.message_login_failed,
                Toast.LENGTH_SHORT)
              .show();
      }
      }
    }.execute(edtUserName.getText().toString(), edtPassword.getText().toString());
  }
}