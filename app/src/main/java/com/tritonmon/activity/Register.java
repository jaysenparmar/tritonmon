package com.tritonmon.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

public class Register extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private Spinner hometown;
    private ImageView registerButton;
    private TextView errorMsg;

    private boolean usernameCleared;
    private boolean passwordCleared;
    private ArrayAdapter hometownAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Constant.DISABLE_ACTION_BAR) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        username = (EditText) findViewById(R.id.registerUsername);
        username.setOnFocusChangeListener(usernameFocusListener);

        password = (EditText) findViewById(R.id.registerPassword);
        password.setOnFocusChangeListener(passwordFocusListener);

        hometown = (Spinner) findViewById(R.id.registerHometown);
        hometownAdapter = ArrayAdapter.createFromResource(this, R.array.hometown_array, android.R.layout.simple_spinner_item);
        hometownAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        hometown.setAdapter(hometownAdapter);

        registerButton = (ImageView) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(clickRegister);


        usernameCleared = false;
        passwordCleared = false;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_register;
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
            }

            if (!hasFocus && password.getText().toString().isEmpty()) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setText(R.string.password);
                passwordCleared = false;
            }
        }
    };

    private View.OnClickListener clickRegister = new View.OnClickListener() {
        public void onClick(View v) {

            if (Audio.isAudioEnabled()) {
                Audio.sfx.start();
            }

            if (username.getText().toString().equals(getString(R.string.username)) || username.getText().toString().isEmpty()) {
                TritonmonToast.makeText(getApplicationContext(),"Please enter a valid username", Toast.LENGTH_LONG).show();
            }
            else if (password.getText().toString().equals(getString(R.string.password)) || password.getText().toString().isEmpty()) {
                TritonmonToast.makeText(getApplicationContext(),"Please enter a valid password", Toast.LENGTH_LONG).show();
            }
            else {
                registerButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "register_btn_en"));
                setProgressBarIndeterminateVisibility(true);

                String passwordHash = Hashing.sha256()
                        .hashString(password.getText().toString(), Charsets.UTF_8)
                        .toString();

                new RegisterTask().execute(username.getText().toString(), passwordHash, hometown.getSelectedItem().toString());
            }

        }
    };

    private class RegisterTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String url = Constant.SERVER_URL + "/adduser/" +
                    Constant.encode(params[0]) + "/" +
                    params[1] + "/" +
                    Constant.encode(params[2]);

            HttpResponse response = MyHttpClient.post(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                List<User> newUsers = MyGson.getInstance().fromJson(MyHttpClient.getJson(response),
                        new TypeToken<List<User>>() {}.getType());
                CurrentUser.setUser(newUsers.get(0));
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent i = new Intent(getApplicationContext(), Welcome.class);
                startActivity(i);
            }
            else {
                registerButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "register_btn_en"));
                setProgressBarIndeterminateVisibility(false);
                TritonmonToast.makeText(getApplicationContext(),"That username is taken", Toast.LENGTH_LONG).show();
            }
        }
    }
}
