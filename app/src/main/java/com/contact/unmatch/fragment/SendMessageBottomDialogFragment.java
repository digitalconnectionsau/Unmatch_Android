package com.contact.unmatch.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.contact.unmatch.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.Nullable;

public class SendMessageBottomDialogFragment extends BottomSheetDialogFragment {

    public static SendMessageBottomDialogFragment newInstance() {
        return new SendMessageBottomDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        BottomSheetDialog dialog = (BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.send_message_dialog, null);
        dialog.setContentView(view);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View)view.getParent());
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
        return dialog;
    }
}
