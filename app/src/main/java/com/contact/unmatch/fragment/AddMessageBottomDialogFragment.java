package com.contact.unmatch.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.contact.unmatch.MainActivity;
import com.contact.unmatch.R;
import com.contact.unmatch.model.MessageCategory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class AddMessageBottomDialogFragment extends BottomSheetDialogFragment {

    private View view;
    private LinearLayout msg_btn_layout;
    private ArrayList<Button> btn_list = new ArrayList<>();
    private ArrayList<MessageCategory> categoryArrayList;
    private int selected_category = 0;
    private Button next_btn;
    private TextView type_textview;
    private TextView preview_textview;
    private TextView preview_message_textview;
    private EditText send_message_edit;
    private Button select_contact;
    private ImageView close_btn;

    public static AddMessageBottomDialogFragment newInstance() {
        return new AddMessageBottomDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle);
        BottomSheetDialog dialog = (BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(),R.layout.bottom_sheet_dialog_layout, null);
        dialog.setContentView(view);
        msg_btn_layout = (LinearLayout) dialog.findViewById(R.id.message_btn_layout);
        next_btn = (Button) dialog.findViewById(R.id.next_button);
        next_btn.setOnClickListener(next_listener);
        close_btn = (ImageView)dialog.findViewById(R.id.close);
        close_btn.setOnClickListener(close_listener);
        type_textview = (TextView) dialog.findViewById(R.id.type_textview);
        preview_textview = (TextView) dialog.findViewById(R.id.preview_textview);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(150));
        preview_message_textview = (TextView) dialog.findViewById(R.id.preview_message_textview);
        preview_message_textview.setLayoutParams(params2);
        send_message_edit = (EditText) dialog.findViewById(R.id.send_message_edit);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(150));
        send_message_edit.setLayoutParams(params3);
        select_contact = (Button) dialog.findViewById(R.id.select_contact);
        select_contact.setOnClickListener(contact_listener);

        for(int i = 0; i < categoryArrayList.size(); i++){
            Button button = new Button(getActivity());
            button.setId(i);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(40));
            params1.setMargins(0, 30, 0, 0);
            button.setLayoutParams(params1);
            button.setText(categoryArrayList.get(i).getCategory());
            button.setTextColor(Color.BLACK);
            button.setBackgroundResource(R.drawable.message_button_background);
            button.setOnClickListener(select_listener);
            btn_list.add(button);
            msg_btn_layout.addView(button);
        }
        //btn_list.get(0).setBackgroundResource(R.drawable.create_button_background);
        //btn_list.get(0).setTextColor(Color.WHITE);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View)view.getParent());
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
        return dialog;
    }

    public int dp2px(float dpValue){
        final float scale = getActivity().getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    private View.OnClickListener select_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i = 0; i < btn_list.size(); i++){
                btn_list.get(i).setTextColor(Color.BLACK);
                btn_list.get(i).setBackgroundResource(R.drawable.message_button_background);
            }
            btn_list.get(v.getId()).setBackgroundResource(R.drawable.create_button_background);
            btn_list.get(v.getId()).setTextColor(Color.WHITE);
            selected_category = v.getId();
            //type_textview.setText(categoryArrayList.get(selected_category).getMessage());
        }
    };

    private View.OnClickListener close_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private View.OnClickListener next_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            type_textview.setVisibility(View.GONE);
//            preview_textview.setVisibility(View.VISIBLE);
            msg_btn_layout.setVisibility(View.GONE);
            preview_message_textview.setVisibility(View.VISIBLE);
            MessageCategory messageCategory = categoryArrayList.get(selected_category);
            preview_message_textview.setText(messageCategory.getMessage());
//            send_message_edit.setVisibility(View.VISIBLE);
            next_btn.setVisibility(View.GONE);
            select_contact.setVisibility(View.VISIBLE);
        }
    };

    public boolean validCellPhone(String number)
    {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public void setCategoryArrayList(ArrayList<MessageCategory> list){
        categoryArrayList = list;
    }

    private View.OnClickListener contact_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MessageCategory messageCategory = categoryArrayList.get(selected_category);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("message_category", (Serializable) messageCategory);
            startActivity(intent);
            dismiss();
        }
    };
}
