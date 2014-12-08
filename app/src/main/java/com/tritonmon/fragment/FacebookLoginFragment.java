package com.tritonmon.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.reflect.TypeToken;
import com.tritonmon.activity.MainMenu;
import com.tritonmon.activity.R;
import com.tritonmon.activity.Welcome;
import com.tritonmon.global.Audio;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.FacebookInfo;
import com.tritonmon.global.singleton.MyGson;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.model.User;

import org.apache.http.HttpResponse;

import java.util.Arrays;
import java.util.List;


public class FacebookLoginFragment extends android.support.v4.app.Fragment {
    protected static final String TAG = "FacebookLoginFragment";

    protected UiLifecycleHelper uiHelper;

    private LinearLayout tritonmonLinearLayout;
    private LinearLayout facebookIconLayout;

    protected Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
        FacebookInfo.session = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_tritonmon, container, false);

        tritonmonLinearLayout = (LinearLayout) view.findViewById(R.id.tritonmonLinearLayout);
        facebookIconLayout = (LinearLayout) view.findViewById(R.id.facebookIconLayout);

        LoginButton button = (LoginButton) view.findViewById(R.id.authButton);
        button.setFragment(this);
        button.setReadPermissions(Arrays.asList("public_profile"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }
                getActivity().setProgressBarIndeterminateVisibility(true);
                tritonmonLinearLayout.setVisibility(View.GONE);
                facebookIconLayout.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {

            if (FacebookInfo.session == null || isSessionChanged(session)) {
                FacebookInfo.session = session;

                Log.d(TAG, "Logged in...");
                getActivity().setProgressBarIndeterminateVisibility(true);
                tritonmonLinearLayout.setVisibility(View.GONE);
                facebookIconLayout.setVisibility(View.VISIBLE);

                Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            FacebookInfo.setFacebookUser(user);
                            new FacebookLoginTask().execute(user.getId(), user.getName());
                        }
                    }
                }).executeAsync();
            }
        }
        else if (state.isClosed()) {
            // this logout only applies for the Tritonmon screen
            // see ActionBarActivity for the logout in the ActionBar
            Log.d(TAG, "Logged out...");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    protected boolean isSessionChanged(Session session) {
        // Check if session state changed
        if (FacebookInfo.session.getState() != session.getState())
            return true;

        // Check if accessToken changed
        if (FacebookInfo.session.getAccessToken() != null) {
            if (!FacebookInfo.session.getAccessToken().equals(session.getAccessToken()))
                return true;
        }
        else if (session.getAccessToken() != null) {
            return true;
        }

        // Nothing changed
        return false;
    }

    protected class FacebookLoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.d("FacebookLoginTask", "STARTED ASYNC TASK");
            Log.d("FacebookLoginTask", "Sending successful Facebook Login to server");

            // try to login first
            String loginUrl = Constant.SERVER_URL + "/getfacebookuser/" + params[0];
            HttpResponse loginResponse = MyHttpClient.get(loginUrl);

            if (MyHttpClient.getStatusCode(loginResponse) == Constant.STATUS_CODE_SUCCESS) {
                // login was successful, set CurrentUser data
                String json = MyHttpClient.getJson(loginResponse);
                List<User> users  = MyGson.getInstance().fromJson(json,
                        new TypeToken<List<User>>() {}.getType());

                if (users.size() > 1) {
                    Log.e("FacebookLoginTask", "GET request expected to only return 1 user but returned " + users.size());
                    return null;
                }
                CurrentUser.setUser(users.get(0));

                Log.d("FacebookLoginTask", "User already exists, logged in");
                return "logged in";
            }
            else if (MyHttpClient.getStatusCode(loginResponse) == Constant.STATUS_CODE_204) {
                // login was unsuccessful because user does not already exist
                // register the new user
                String registerUrl = Constant.SERVER_URL + "/addfacebookuser/" + params[0] + "/" + Constant.encode(params[1]);
                HttpResponse registerResponse = MyHttpClient.post(registerUrl);

                if (MyHttpClient.getStatusCode(registerResponse) == Constant.STATUS_CODE_SUCCESS) {
                    // register new user was successful, set CurrentUser data
                    String json = MyHttpClient.getJson(registerResponse);
                    List<User> newUsers = MyGson.getInstance().fromJson(json,
                            new TypeToken<List<User>>() {}.getType());

                    CurrentUser.setUser(newUsers.get(0));

                    Log.d("FacebookLoginTask", "User does not exist, registered user");
                    return "registered";
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) { // nothing went wrong
                if (result.equals("registered")) { // user registered, go to Welcome
                        Log.d("FacebookLoginTask", "Redirecting to Welcome");
                        Intent i = new Intent(getActivity().getApplicationContext(), Welcome.class);
                        startActivity(i);
                }
                else if (result.equals("logged in")) { // user logged in, go to MainMemnu
                    if (!(getActivity() instanceof MainMenu)) {
                        Log.d("FacebookLoginTask", "Redirecting to MainMenu");
                        Intent i = new Intent(getActivity().getApplicationContext(), MainMenu.class);
                        startActivity(i);
                    }
                    else {
                        Log.d("FacebookLoginTask", "Already on MainMenu, not redirecting");
                    }
                }
            }
            else { // something went wrong
                Log.e("FacebookLoginTask", "Something went wrong...");
            }
        }
    }

}
