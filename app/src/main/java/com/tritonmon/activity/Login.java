package com.tritonmon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.gson.reflect.TypeToken;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.MyGson;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.User;

import org.apache.http.HttpResponse;

import java.util.List;


public class Login extends Activity {

    private EditText username;
    private EditText password;
    private ImageButton loginButton;
    private TextView errorMsg;

    private boolean usernameCleared;
    private boolean passwordCleared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.loginUsername);
        username.setOnFocusChangeListener(usernameFocusListener);

        password = (EditText) findViewById(R.id.loginPassword);
        password.setOnFocusChangeListener(passwordFocusListener);

        loginButton = (ImageButton) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(clickLogin);

        errorMsg = (TextView) findViewById(R.id.errorMsg);

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
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
            errorMsg.setText("");
            String passwordHash = Hashing.sha256()
                    .hashString(password.getText().toString(), Charsets.UTF_8)
                    .toString();
            new VerifyUserTask().execute(username.getText().toString(), passwordHash);
        }
    };

    private class VerifyUserTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String url = Constant.SERVER_URL + "/getuser/" + Constant.encode(params[0]);

            HttpResponse response = MyHttpClient.get(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                String json = MyHttpClient.getJson(response);

                List<User> proposedUsers = MyGson.getInstance().fromJson(json,
                        new TypeToken<List<User>>() {}.getType());

                if (proposedUsers.size() > 1) {
                    Log.e("Login", "GET request expected to only return 1 user but returned " + proposedUsers.size());
                    return false;
                }

                User proposedUser = proposedUsers.get(0);
                if (proposedUser.getPassword().equals(params[1])) {
                    CurrentUser.setUser(proposedUser);
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent i = new Intent(getApplicationContext(), MainMenu.class);
                i.putExtra("loggedIn", true);
                startActivity(i);
            }
            else {
                errorMsg.setText("Username and password were incorrect.");
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

}
