package com.tritonmon.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.gson.reflect.TypeToken;
import com.tritonmon.global.Audio;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.singleton.MyGson;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.global.util.ImageUtil;
import com.tritonmon.model.User;
import com.tritonmon.toast.TritonmonToast;

import org.apache.http.HttpResponse;

import java.util.List;


public class Login extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private ImageView loginButton;

    private boolean usernameCleared;
    private boolean passwordCleared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Constant.DISABLE_ACTION_BAR) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        username = (EditText) findViewById(R.id.loginUsername);
        username.setOnFocusChangeListener(usernameFocusListener);

        password = (EditText) findViewById(R.id.loginPassword);
        password.setOnFocusChangeListener(passwordFocusListener);

        loginButton = (ImageView) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(clickLogin);

        usernameCleared = false;
        passwordCleared = false;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.logged_out_menu;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private View.OnFocusChangeListener usernameFocusListener = new View.OnFocusChangeListener() {
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

    private View.OnFocusChangeListener passwordFocusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && !passwordCleared) {
                passwordCleared = true;
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setText("");
                password.setTypeface(username.getTypeface());
            }

            if (!hasFocus && password.getText().length() == 0) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setText(R.string.password);
                passwordCleared = false;
                password.setTypeface(username.getTypeface());
            }
        }
    };

    private View.OnClickListener clickLogin = new View.OnClickListener() {
        public void onClick(View v) {

            if (Audio.isAudioEnabled()) {
                Audio.sfx.start();
            }

            loginButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "login_btn_en"));
            setProgressBarIndeterminateVisibility(true);

            String passwordHash = Hashing.sha256()
                    .hashString(password.getText().toString(), Charsets.UTF_8)
                    .toString();

            new LoginTask().execute(username.getText().toString(), passwordHash);
        }
    };

    private class LoginTask extends AsyncTask<String, Void, Boolean> {

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
                startActivity(i);
            }
            else {
                loginButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "login_btn"));
                setProgressBarIndeterminateVisibility(false);
                TritonmonToast.makeText(getApplicationContext(), "Incorrect username and password", Toast.LENGTH_LONG).show();
            }
        }
    }
}
