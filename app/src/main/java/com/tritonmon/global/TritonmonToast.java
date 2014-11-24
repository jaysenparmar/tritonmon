package com.tritonmon.global;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.activity.R;

public class TritonmonToast extends Toast {

    public TritonmonToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, String message, int length) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toastLayout = inflater.inflate(R.layout.toast, null);

        TextView toastTextView = (TextView) toastLayout.findViewById(R.id.toast_text);
        toastTextView.setText(message);

        Toast toast = new Toast(context);
        toast.setDuration(length);
        toast.setView(toastLayout);

        return toast;
    }
}
