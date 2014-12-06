package com.tritonmon.asynctask.trades;

import android.os.AsyncTask;
import android.util.Log;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.model.Trade;

import org.apache.http.HttpResponse;

import java.util.List;

public class SetViewedDecisions extends AsyncTask<Void, Void, Boolean> {

    private int offererUsersId;

    public SetViewedDecisions(int offererUsersId) {
        this.offererUsersId = offererUsersId;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("asynctask/SetViewedDecisions", "STARTED ASYNC TASK");
        Log.d("asynctask/SetViewedDecisions", "Sending offererUsersId " + offererUsersId + " to server");

        String url = Constant.SERVER_URL + "/setseendecisions/" + offererUsersId;
        HttpResponse response = MyHttpClient.post(url);
        setSeenDecisions();
        if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
            url = Constant.SERVER_URL + "/removeseendecisions";
            response = MyHttpClient.post(url);
            removeSeenDecisions();
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                return true;
            }
        }
        Log.e("asynctask/SetViewedDecisions", response.getStatusLine().toString());
        return false;
    }

    private void setSeenDecisions() {
        List<Trade> tmp = CurrentUser.getTrades();
        for (Trade ele : tmp) {
            if (ele.getOffererUsersId() == offererUsersId && ele.isDeclined() && !ele.isSeenDecline()) {
                ele.setSeenDecline(true);
            }
            else if (ele.getOffererUsersId() == offererUsersId && ele.isAccepted() && !ele.isSeenAcceptance()) {
                ele.setSeenAcceptance(true);
            }
        }
        CurrentUser.setTrades(tmp);
    }

    private void removeSeenDecisions() {
        List<Trade> tmp = CurrentUser.getTrades();
        for (Trade ele : CurrentUser.getTrades()) {
            if (ele.isDeclined() && ele.isSeenDecline()) {
                tmp.remove(ele);
            }
            else if (ele.isAccepted() && ele.isSeenAcceptance()) {
                tmp.remove(ele);
            }
        }
        CurrentUser.setTrades(tmp);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("asynctask/SetViewedDecisions", "FINISHED ASYNC TASK");
    }


}
