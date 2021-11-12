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


public class MenuFragment extends Fragment {

    private View view;
    private static MenuFragment instance = null;
    private RecyclerView contact_history_recycler;
    private ContactHistoryAdapter mAdapter;
    private List<ContactInfo> contacts = new ArrayList<>();
    private ImageView add_message_view;
    private ArrayList<MessageCategory> category_list;
    private ArrayList<Exes> exes_list;
    private ProgressBar progressBar;

    public MenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        contact_history_recycler = (RecyclerView) view.findViewById(R.id.contact_history_recycler);
        contact_history_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        add_message_view = (ImageView) view.findViewById(R.id.add);
        add_message_view.setOnClickListener(add_listener);
        //category_list = (ArrayList<MessageCategory>) getArguments().getSerializable("message_category");
        //exes_list = (ArrayList<Exes>) getArguments().getSerializable("exes_list");

        getMessageCategory();
        return view;
    }

    public void updateUI() {
//        contacts = new ArrayList<>();
        if (mAdapter == null) {
            mAdapter = new ContactHistoryAdapter(exes_list);
            contact_history_recycler.setAdapter(mAdapter);

        } else {
            mAdapter.setContacts(exes_list);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(mAdapter.getPosition());
        }
    }

    private View.OnClickListener add_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AddMessageBottomDialogFragment addMessageBottomDialogFragment = AddMessageBottomDialogFragment.newInstance();
            addMessageBottomDialogFragment.setCategoryArrayList(category_list);
            addMessageBottomDialogFragment.show(getChildFragmentManager(), "add_contact");
        }
    };

    public void getMessageCategory() {
        category_list = new ArrayList<>();
        exes_list = new ArrayList<>();
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
                Toast.makeText(getActivity(), R.string.message_unknown_error, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), R.string.message_invalid_token, Toast.LENGTH_SHORT).show();
                            User.deleteUser();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                } else if (!TextUtils.isEmpty(responseString.trim())) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), responseString.trim(), Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
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

                    updateUI();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), R.string.message_unknown_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("api_result", "" + statusCode + " : " + responseString);

                if (!TextUtils.isEmpty(responseString.trim())) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), responseString.trim(), Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class ContactHistoryAdapter extends RecyclerView.Adapter<ContactHistoryHolder> {

        private List<Exes> mExes;
        private List<String> sections;
        private LinearLayout layout;
        private int position;

        public ContactHistoryAdapter(List<Exes> exes) {
            mExes = exes;
            setHasStableIds(true);
        }

        @Override
        public ContactHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_contact_history, parent, false);
            return new ContactHistoryHolder(view, this);
        }

        @Override
        public void onBindViewHolder(ContactHistoryHolder holder, int position) {
            Exes exes = mExes.get(position);
//            String contact_string = "" + contact.getName().charAt(0);
            holder.bindContact(exes, position);
        }

        @Override
        public int getItemCount() {
            return mExes.size();
        }

        public void setContacts(List<Exes> exes) {
            mExes = exes;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }

    public class ContactHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mNameTextView;
        private ImageView mPhotoView;
        private TextView mSectionTextView;
        private Exes mExes;
        private ContactHistoryAdapter mAdapter;
        public LinearLayout item_layout;
        public CardView contact_card;

        public ContactHistoryHolder(View itemView, ContactHistoryAdapter adaptor) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            mPhotoView = (ImageView) itemView.findViewById(R.id.contact_icon);
            contact_card = (CardView) itemView.findViewById(R.id.contact_card);
            contact_card.setAlpha(0.6f);
            itemView.setOnClickListener(this);
            mAdapter = adaptor;
        }

        public void bindContact(Exes exes, int position) {
            mExes = exes;
            mNameTextView.setText(mExes.getName());
        }

        @Override
        public void onClick(View v) {
            getMessages();
            //String contact_string = "" + mNameTextView.getText().toString().charAt(0);
            //Intent intent = new Intent(getActivity(), ContactDetail.class);
            //intent.putExtra("extra_contact", mContact);
            //startActivity(intent);
        }

        private void getMessages() {
            String url = HttpUtils.API_URL + "Message/" + mExes.getId();
            RequestParams requestParams = new RequestParams();
            progressBar.setVisibility(View.VISIBLE);
            HttpUtils.get(url, requestParams, Utils.getAccessToken(), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray data_array) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        ArrayList<Message> message_list = new ArrayList<>();
                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject obj = data_array.getJSONObject(i);
                            String contents = obj.getString("messageBody");
                            String dateUTC = obj.getString("dateUTC");
                            Message message = new Message(contents, dateUTC);
                            message_list.add(message);

                        }
                        Intent intent = new Intent(getActivity(), ExeDetailActivity.class);
                        intent.putExtra("detail_message", message_list);
                        startActivity(intent);
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getActivity(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("api_result", "" + statusCode + " : " + responseString);
                    if (statusCode == 401) {
                        HttpUtils.refreshtoken(new RefreshTokenListener() {
                            @Override
                            public void onSuccessRefreshToken() {
                                progressBar.setVisibility(View.GONE);
                                getMessages();
                            }

                            @Override
                            public void onFailureRefreshToken() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), R.string.message_invalid_token, Toast.LENGTH_SHORT).show();
                                User.deleteUser();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                    } else if (statusCode == 200) {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(getActivity(), CloseActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (!TextUtils.isEmpty(responseString.trim())) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), responseString, Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}