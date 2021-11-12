package com.contact.unmatch.home;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.contact.unmatch.R;
import com.contact.unmatch.authentication.LoginActivity;
import com.contact.unmatch.fragment.MenuFragment;
import com.contact.unmatch.fragment.MessagesFragment;
import com.contact.unmatch.fragment.SettingsFragment;
import com.contact.unmatch.model.Exes;
import com.contact.unmatch.model.MessageCategory;
import com.contact.unmatch.model.User;
import com.contact.unmatch.utils.HttpUtils;
import com.contact.unmatch.utils.RefreshTokenListener;
import com.contact.unmatch.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    public Bundle m_savedInstanceState;
    private MenuFragment menuFragment;
    private MessagesFragment messagesFragment;
    private SettingsFragment settingsFragment;
    private ArrayList<MessageCategory> category_list = new ArrayList<>();
    private ArrayList<Exes> exes_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        m_savedInstanceState = savedInstanceState;

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(R.id.action_menu).setChecked(true);
        /*bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_menu:
                        break;
                    case R.id.action_chat:
                        break;
                    case R.id.action_setting:
                        break;
                }
                return true;
            }
        });*/

        menuFragment = new MenuFragment();
        messagesFragment = new MessagesFragment();
        settingsFragment = new SettingsFragment();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                int itemId = item.getItemId();
                if (itemId == R.id.action_menu) {
                    fragmentTransaction.replace(R.id.fragment_container, menuFragment).commitAllowingStateLoss();
                } else if (itemId == R.id.action_chat) {
                    fragmentTransaction.replace(R.id.fragment_container, messagesFragment).commitAllowingStateLoss();
                } else if (itemId == R.id.action_setting) {
                    fragmentTransaction.replace(R.id.fragment_container, settingsFragment).commitAllowingStateLoss();
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_menu);


        //getMessageCategory();
    }

    /*public void getMessageCategory() {
        progressBar.setVisibility(View.VISIBLE);
        String url = HttpUtils.API_URL + "Message/BreakupMessages";
        RequestParams rp = new RequestParams();
        HttpUtils.get(url, rp, Utils.getAccessToken(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray data_array) {
                try {
                    for (int i = 0; i < data_array.length(); i++) {
                        JSONObject obj = data_array.getJSONObject(i);
                        String category = obj.getString("category");
                        JSONArray message_dto = obj.getJSONArray("breakupMessageDTOs");
                        for (int j = 0; j < message_dto.length(); j++) {
                            JSONObject message_obj = message_dto.getJSONObject(j);
                            int messageId = message_obj.getInt("messageId");
                            String message = message_obj.getString("message");
                            MessageCategory msg_category = new MessageCategory(category, messageId, message);
                            category_list.add(msg_category);
                        }
                    }

                    getExesList();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(getApplicationContext(), R.string.message_unknown_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("api_result", "" + statusCode + " : " + responseString);

                if (statusCode == 401) {
                    HttpUtils.refreshtoken(new RefreshTokenListener() {
                        @Override
                        public void onSuccessRefreshToken() {
                            progressBar.setVisibility(View.GONE);
                            getMessageCategory();
                        }

                        @Override
                        public void onFailureRefreshToken() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), R.string.message_invalid_token, Toast.LENGTH_SHORT).show();
                            User.deleteUser();
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else if (!TextUtils.isEmpty(responseString.trim())) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), responseString.trim(), Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getExesList() {
        //progressBar.setVisibility(View.VISIBLE);
        String url = HttpUtils.API_URL + "Exes";
        RequestParams rp = new RequestParams();
        HttpUtils.get(url, rp, Utils.getAccessToken(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray data_array) {
                progressBar.setVisibility(View.GONE);
                try {
                    for (int i = 0; i < data_array.length(); i++) {
                        JSONObject obj = data_array.getJSONObject(i);
                        int id = obj.getInt("id");
                        String name = obj.getString("name");
                        String phoneNumber = obj.getString("phoneNumber");
                        int breakupMessageId = obj.getInt("breakupMessageId");
                        Exes exes = new Exes(id, name, phoneNumber, breakupMessageId);
                        exes_list.add(exes);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("message_category", category_list);
                    bundle.putSerializable("exes_list", exes_list);
                    menuFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, menuFragment).commit();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), R.string.message_unknown_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("api_result", "" + statusCode + " : " + responseString);

                if (!TextUtils.isEmpty(responseString.trim())) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), responseString.trim(), Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
}