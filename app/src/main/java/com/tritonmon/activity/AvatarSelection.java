package com.tritonmon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.tritonmon.asynctask.user.UpdateAvatar;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.util.ImageUtil;

import java.util.List;


// todo: add dialog or toast?
public class AvatarSelection extends ActionBarActivity {

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
                adapter.notifyDataSetChanged();
            }
        });
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
    }

    private class AvatarSelectionAdapter extends ArrayAdapter<String> {

        public AvatarSelectionAdapter(List<String> list) {
            super(AvatarSelection.this, R.layout.avatar_item_layout, R.id.tmpTextView, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            if (v != convertView && v != null) {
                final ViewHolder holder = new ViewHolder();
                holder.avatarImage = (ImageView) v.findViewById(R.id.avatarImage);
                v.setTag(holder);
            }

            final ViewHolder holder = (ViewHolder) v.getTag();
            final String avatar = getItem(position);

            holder.avatarImage.setImageResource(ImageUtil.getImageResource(getApplicationContext(), avatar));
            holder.avatarImage.setOnClickListener(new ImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new UpdateAvatar(avatar).execute();
                    Intent i = new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(i);
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
