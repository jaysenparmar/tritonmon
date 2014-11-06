package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class Login extends Activity {

    public final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;

    private boolean usernameCleared;
    private boolean passwordCleared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        usernameInput.setOnFocusChangeListener(usernameFocusListener);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        passwordInput.setOnFocusChangeListener(passwordFocusListener);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(clickLogin);

        usernameCleared = false;
        passwordCleared = false;

//        getCurrentFocus().clearFocus();
    }

    View.OnFocusChangeListener usernameFocusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus && !usernameCleared) {
                usernameCleared = true;
                usernameInput.setText("");
            }

            if (!hasFocus && usernameInput.getText().length() == 0) {
                usernameInput.setText(R.string.username);
                usernameCleared = false;
            }
        }
    };

    View.OnFocusChangeListener passwordFocusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && !passwordCleared) {
                passwordCleared = true;
                passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordInput.setText("");
            }

            if (!hasFocus && passwordInput.getText().length() == 0) {
                passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordInput.setText(R.string.password);
                passwordCleared = false;
            }
        }
    };

    View.OnClickListener clickLogin = new View.OnClickListener() {
        public void onClick(View v) {
            new VerifyUser().execute(
                    usernameInput.getText().toString(),
                    passwordInput.getText().toString());
        }
    };

    private class VerifyUser extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String rootUrl = "http://ec2-54-193-111-74.us-west-1.compute.amazonaws.com:8080";
            String appUrl = "/tritonmon-server";
            String queryUrl = "/adduser/" + params[0] + "/" + params[1] + "/M/test_town";
            String url = rootUrl + appUrl + queryUrl;

            Log.i("request", url);

            HttpClient httpclient = new DefaultHttpClient();

            // Prepare a request object
            HttpGet httpget = new HttpGet(url);

            // Execute the request
            HttpResponse response;
            try {
                response = httpclient.execute(httpget);
                // Examine the response status
                Log.i("response", response.getStatusLine().toString());
                return response.getStatusLine().getStatusCode() != STATUS_CODE_INTERNAL_SERVER_ERROR;
            } catch (Exception e) { // FIXME should not be catching all exceptions
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent i = new Intent(getApplicationContext(), Welcome.class);
                i.putExtra("username", usernameInput.getText().toString());
                startActivity(i);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_avatar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_avatar, container, false);
            return rootView;
        }
    }
}
