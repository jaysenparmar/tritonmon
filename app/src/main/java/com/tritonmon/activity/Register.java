package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
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
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.User;

import org.apache.http.HttpResponse;

import java.util.List;

public class Register extends Activity {

    private EditText username;
    private EditText password;
    private Button registerButton;

    private boolean usernameCleared;
    private boolean passwordCleared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        username = (EditText) findViewById(R.id.registerUsername);
        username.setOnFocusChangeListener(usernameFocusListener);
        password = (EditText) findViewById(R.id.registerPassword);
        password.setOnFocusChangeListener(passwordFocusListener);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(clickRegister);

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

    View.OnClickListener clickRegister = new View.OnClickListener() {
        public void onClick(View v) {
            new AddUser().execute(
                    username.getText().toString(),
                    password.getText().toString());
        }
    };

    private class AddUser extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String url = Constant.SERVER_URL + "/adduser/" + params[0] + "/" + params[1] + "/M/test_town";

            HttpResponse response = MyHttpClient.post(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                List<User> newUsers = MyGson.getInstance().fromJson(MyHttpClient.getJson(response), new TypeToken<List<User>>() {}.getType());
                User newUser = newUsers.get(0);
                CurrentUser.setUser(newUser);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_register, container, false);
            return rootView;
        }
    }
}
