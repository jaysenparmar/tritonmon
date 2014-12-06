package com.tritonmon.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

public class Register extends Activity {

    private EditText username;
    private EditText password;
    private Spinner hometown;
    private ImageButton registerButton;
    private TextView errorMsg;

    private boolean usernameCleared;
    private boolean passwordCleared;
    private ArrayAdapter hometownAdapter;

    private MediaPlayer sfx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.registerUsername);
        username.setOnFocusChangeListener(usernameFocusListener);

        password = (EditText) findViewById(R.id.registerPassword);
        password.setOnFocusChangeListener(passwordFocusListener);

        hometown = (Spinner) findViewById(R.id.registerHometown);
        hometownAdapter = ArrayAdapter.createFromResource(this, R.array.hometown_array, android.R.layout.simple_spinner_item);
        hometownAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        hometown.setAdapter(hometownAdapter);

        registerButton = (ImageButton) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(clickRegister);

        errorMsg = (TextView) findViewById(R.id.errorMsg);

        usernameCleared = false;
        passwordCleared = false;

        if(sfx != null) {
            sfx.release();
        }

        sfx = MediaPlayer.create(getApplicationContext(), R.raw.choose);
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

            if (!hasFocus && password.getText().toString().isEmpty()) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setText(R.string.password);
                passwordCleared = false;
            }
        }
    };

    View.OnClickListener clickRegister = new View.OnClickListener() {
        public void onClick(View v) {
            String error = "";

            sfx.start();
            if (username.getText().toString().equals(getString(R.string.username)) || username.getText().toString().isEmpty()) {
                error += "Please select a valid username.";
            }
            if (password.getText().toString().equals(getString(R.string.password)) || password.getText().toString().isEmpty()) {
                if (!error.isEmpty()) {
                    error += "\n";
                }
                error += "Please select a password.";
            }

            if (error.isEmpty()) {
                String passwordHash = Hashing.sha256()
                        .hashString(password.getText().toString(), Charsets.UTF_8)
                        .toString();
                new AddUserTask().execute(username.getText().toString(), passwordHash, hometown.getSelectedItem().toString());
            }
            else {
                errorMsg.setText(error);
            }

        }
    };

    private class AddUserTask extends AsyncTask<String, Void, Boolean> {

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
                errorMsg.setText("That username is already taken!");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Tritonmon.class);
        startActivity(i);
    }
}
