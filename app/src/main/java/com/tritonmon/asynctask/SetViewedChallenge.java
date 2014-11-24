package com.tritonmon.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ListUtil;
import com.tritonmon.global.MyHttpClient;

import org.apache.http.HttpResponse;

import java.util.List;

public class SetViewedChallenge extends AsyncTask<Void, Void, Boolean> {

    public enum Choices {
        DECLINED,
        NEUTRAL,
//        ACCEPTED
    }
    private String challengerUsername;
//    private List<String> challengerUsername;
    private String challengedUsername;
    private Choices choice;
//    private List<Choices> choice;

    public SetViewedChallenge(String challengerUsername, String challengedUsername, Choices choice) {
//    public SetViewedChallenge(List<String> challengerUsername, String challengedUsername, List<Choices> choice) {
        this.challengerUsername = challengerUsername;
        this.challengedUsername = challengedUsername;
        this.choice = choice;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/SetViewedChallenge", "STARTED ASYNC TASK");
        Log.d("asynctask/SetViewedChallenge", "Sending challenger " + challengerUsername + ", Sending challenged " + challengedUsername + ", choice: " + choice + " to server");
        String url = "";
        switch (choice) {
            case DECLINED:
                url = Constant.SERVER_URL + "/setdeclinechallenge/";
                break;
            case NEUTRAL:
                url = Constant.SERVER_URL + "/setseenchallenge/";
                break;
//            case ACCEPTED:
//                url = Constant.SERVER_URL + "/setacceptchallenge/";
//                break;
            default:
                return false;
        }

        url+=challengerUsername+"/"+challengedUsername;
        HttpResponse response = MyHttpClient.post(url);
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            return true;
        }

        Log.e("asynctask/SetViewedChallenge", response.getStatusLine().toString());
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/SetViewedChallenge", "FINISHED ASYNC TASK");
    }


}
