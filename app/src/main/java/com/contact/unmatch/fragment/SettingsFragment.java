package com.contact.unmatch.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.contact.unmatch.R;
import com.contact.unmatch.authentication.AuthenticationActivity;
import com.contact.unmatch.authentication.LoginActivity;
import com.contact.unmatch.model.User;
import com.contact.unmatch.utils.Utils;


public class SettingsFragment extends Fragment {

    private View view;
    private static SettingsFragment instance = null;
    private ProgressBar progressBar;

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        view.findViewById(R.id.btn_signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignoutDialog();
            }
        });
        return view;
    }

    private void showSignoutDialog() {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                //.setTitle("Block")
                .setMessage(R.string.message_signout)
                .setPositiveButton(R.string.signout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                })
                .setNegativeButton(R.string.title_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void signOut() {
        Utils.removeAccessToken();
        Utils.removeRefreshToken();
        User.deleteUser();

        Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}