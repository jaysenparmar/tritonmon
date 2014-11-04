package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Tritonmon extends Activity {

    private Button fbLogin;
    private Button defLogin;
    private TextView jsonText;

    private void init() {
        fbLogin = (Button) findViewById(R.id.fb_login_button);
        defLogin = (Button) findViewById(R.id.default_login_button);
        jsonText = (TextView) findViewById(R.id.json_text_view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tritonmon);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        init();

        fbLogin.setOnClickListener(new OnClickListener(){
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), FacebookLogin.class);
                startActivity(i);
            }
        });

        defLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CreateAvatar.class);
                startActivity(i);
            }
        });

        new TestDatabase().execute();

//        Intent intent = getIntent();
//        String message = intent.getStringExtra(TestDatabase.EXTRA_MESSAGE);
//        jsonText.setText(message);
//        setContentView(jsonText);
    }

<<<<<<< HEAD
=======
    public void onClick(View view) {
    }

>>>>>>> origin/master
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tritonmon, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_tritonmon, container, false);
            return rootView;
        }
    }

    private class TestDatabase extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String rootUrl = "http://ec2-54-193-111-74.us-west-1.compute.amazonaws.com:8080";
            String appUrl = "/tritonmon-server";
            String queryUrl = "/table=pokemon";
            String url = rootUrl + appUrl + queryUrl;

            System.out.println("query : " + url);

            HttpClient httpclient = new DefaultHttpClient();

            // Prepare a request object
            HttpGet httpget = new HttpGet(url);

            // Execute the request
            HttpResponse response;
            try {
                response = httpclient.execute(httpget);
                // Examine the response status
                Log.i("Praeda", response.getStatusLine().toString());

                // Get hold of the response entity
                HttpEntity entity = response.getEntity();
                // If the response does not enclose an entity, there is no need
                // to worry about connection release

                if (entity != null) {
                    // A Simple JSON Response Read
                    InputStream instream = entity.getContent();
                    // now you have the string representation of the HTML request
                    // instream.close();

                    String json =  convertStreamToString(instream);
                    instream.close();
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // FIXME should not be catching all exceptions

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
//            Intent intent = new Intent(getApplicationContext(), Tritonmon.class);
//            intent.putExtra(EXTRA_MESSAGE, result);
//            startActivity(intent);
            jsonText.setText(result);
        }

        // from http://stackoverflow.com/questions/4457492/how-do-i-use-the-simple-http-client-in-android
        private String convertStreamToString(InputStream is) {
            /*
             * To convert the InputStream to String we use the BufferedReader.readLine()
             * method. We iterate until the BufferedReader return null which means
             * there's no more data to read. Each line will appended to a StringBuilder
             * and returned as String.
             */
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return sb.toString();
        }

    }

}
