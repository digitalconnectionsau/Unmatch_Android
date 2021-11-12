package com.contact.unmatch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.contact.unmatch.ContactInfo;
import com.contact.unmatch.R;
import com.contact.unmatch.authentication.LoginActivity;
import com.contact.unmatch.home.CloseActivity;
import com.contact.unmatch.home.ExeDetailActivity;
import com.contact.unmatch.model.Exes;
import com.contact.unmatch.model.Message;
import com.contact.unmatch.model.MessageCategory;
import com.contact.unmatch.model.User;
import com.contact.unmatch.utils.HttpUtils;
import com.contact.unmatch.utils.RefreshTokenListener;
import com.contact.unmatch.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MessagesFragment extends Fragment {

    private View view;
    private static MessagesFragment instance = null;
    private ProgressBar progressBar;

    public MessagesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        progressBar = view.findViewById(R.id.progressBar);

        return view;
    }
}