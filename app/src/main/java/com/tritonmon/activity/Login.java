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

import com.google.gson.reflect.TypeToken;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.MyGson;
import com.tritonmon.model.User;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URLEncoder;
import java.util.List;


public class Login extends Activity {

    private EditText username;
    private EditText password;
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

        username = (EditText) findViewById(R.id.loginUsername);
        username.setOnFocusChangeListener(usernameFocusListener);
        password = (EditText) findViewById(R.id.loginPassword);
        password.setOnFocusChangeListener(passwordFocusListener);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(clickLogin);

        usernameCleared = false;
        passwordCleared = false;
    }

    View.OnFocusChangeListener usernameFocusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus && !usernameCleared) {
                usernameCleared = true;
                username.setText("");
            }

            if (!hasFocus && username.getText().length() == 0) {
                username.setText(R.string.username);
                usernameCleared = false;
            }
        }
    };

    View.OnFocusChangeListener passwordFocusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && !passwordCleared) {
                passwordCleared = true;
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                password.setText("");
            }

            if (!hasFocus && password.getText().length() == 0) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setText(R.string.password);
                passwordCleared = false;
            }
        }
    };

    View.OnClickListener clickLogin = new View.OnClickListener() {
        public void onClick(View v) {
            new VerifyUser().execute(
                    username.getText().toString(),
                    password.getText().toString());
        }
    };

    private class VerifyUser extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String url = null;
            try {
                url = Constant.SERVER_URL + "/table=users/column=username/value=" + URLEncoder.encode("\"" + params[0] + "\"", "UTF-8");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("request", url);

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);

            // Execute the request
            HttpResponse response;
            try {
                response = httpclient.execute(httpget);
                if (response == null) {
                    return false;
                }
                HttpEntity entity = response.getEntity();

                Log.d("response", response.getStatusLine().toString());

                String json = IOUtils.toString(entity.getContent(), "UTF-8");

                if (json.isEmpty()) {
                    Log.d("response", "IS EMPTY");
                    return false;
                }
                else {
                    Log.d("response", json);
                }

                List<User> proposedUsers = MyGson.getInstance().fromJson(json, new TypeToken<List<User>>() {}.getType());
                User proposedUser = proposedUsers.get(0);
                if (proposedUser.getPassword().equals(params[1])) {
                    CurrentUser.setUser(proposedUser);
                    return true;
                }

                return false;

            } catch (Exception e) { // FIXME should not be catching all exceptions
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent i = new Intent(getApplicationContext(), Welcome.class);
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
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            return rootView;
        }
    }
}
