package com.tritonmon.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.asynctask.trades.GetTrades;
import com.tritonmon.asynctask.trades.TradePlayer;
import com.tritonmon.asynctask.user.UpdateAvatar;
import com.tritonmon.fragment.dialog.ConfirmTradeDialog;
import com.tritonmon.fragment.dialog.InvalidTradeDialog;
import com.tritonmon.fragment.dialog.ViewAcceptanceDialog;
import com.tritonmon.fragment.dialog.ViewUpdatedAvatarDialog;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.util.ImageUtil;
import com.tritonmon.global.util.TradingUtil;
import com.tritonmon.model.TradingUser;
import com.tritonmon.model.UsersPokemon;
import com.tritonmon.toast.TritonmonToast;

import java.util.ArrayList;
import java.util.List;


// todo: add dialog or toast?
public class AvatarSelection extends ActionBarActivity implements ViewUpdatedAvatarDialog.NoticeDialogListener {

    private GridView gridView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gridView = (GridView) findViewById(R.id.avatarSelectionGridView);
        if (CurrentUser.getUser().getGender().equals("F")) {
            adapter = new AvatarSelectionAdapter(Constant.femaleAvatars);
        } else {
            adapter = new AvatarSelectionAdapter(Constant.maleAvatars);
        }
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String avatar = (String) gridView.getItemAtPosition(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void showViewUpdatedAvatarDialog() {
        DialogFragment dialog = new ViewUpdatedAvatarDialog();
        dialog.show(getFragmentManager(), "ViewUpdatedAvatarDialog");
    }

    @Override
    public void onViewUpdatedAvatarDialogPositiveClick(DialogFragment dialog) {
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_avatar_selection;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.logged_in_menu;
    }

    private class ViewHolder {
        public ImageView avatarImage;
        public TextView tmpTextView;
    }

    private class AvatarSelectionAdapter extends ArrayAdapter<String> {

        public AvatarSelectionAdapter(List<String> list) {
            super(AvatarSelection.this, R.layout.avatar_item_layout, R.id.tmpTextView, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                Log.e("avatar selection", "NOT NULL");
                final ViewHolder holder = new ViewHolder();
                holder.avatarImage = (ImageView) v.findViewById(R.id.avatarImage);
                v.setTag(holder);
            }

            final ViewHolder holder = (ViewHolder) v.getTag();
            final String avatar = getItem(position);
//            Log.e("avatar selection", avatar);
//            holder.avatarImage.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "maletrainer002"));
            holder.avatarImage.setImageResource(ImageUtil.getImageResource(getApplicationContext(), avatar));
            holder.avatarImage.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new UpdateAvatar(avatar).execute();
                    showViewUpdatedAvatarDialog();

                }
            });

            return v;
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(i);
    }

}
