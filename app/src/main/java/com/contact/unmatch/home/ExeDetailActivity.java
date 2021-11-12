package com.contact.unmatch.home;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.contact.unmatch.R;
import com.contact.unmatch.model.Message;

import java.util.ArrayList;

public class ExeDetailActivity extends AppCompatActivity {

    ArrayList<Message> message_list;
    private LinearLayout message_layout;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exe_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        message_list = (ArrayList<Message>) intent.getSerializableExtra("detail_message");
        back = findViewById(R.id.back_icon);
        back.setOnClickListener(back_listener);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.6);
        ListView simpleList;
        simpleList = findViewById(R.id.simpleListView);
        ListAdapter arrayAdapter = new ListAdapter(this, message_list, width);
        simpleList.setAdapter(arrayAdapter);
    }

    private View.OnClickListener back_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}