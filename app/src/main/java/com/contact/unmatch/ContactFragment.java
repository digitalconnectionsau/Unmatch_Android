package com.contact.unmatch;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.ROLE_SERVICE;
import static android.content.Context.TELECOM_SERVICE;
import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;

import android.app.Activity;
import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BlockedNumberContract;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.contact.unmatch.authentication.LoginActivity;
import com.contact.unmatch.home.CloseActivity;
import com.contact.unmatch.model.MessageCategory;
import com.contact.unmatch.model.User;
import com.contact.unmatch.utils.HttpUtils;
import com.contact.unmatch.utils.RefreshTokenListener;
import com.contact.unmatch.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import jagerfield.mobilecontactslibrary.Contact.Contact;
import jagerfield.mobilecontactslibrary.ElementContainers.EmailContainer;
import jagerfield.mobilecontactslibrary.ElementContainers.NumberContainer;
import jagerfield.mobilecontactslibrary.ImportContactsAsync;

public class ContactFragment extends Fragment {

    private View view;
    private RecyclerView mContactRecyclerView;
    private static ContactFragment instance = null;
    private List<ContactInfo> contacts = new ArrayList();
    public List<ContactInfo> all_contact = new ArrayList<>();
    private ContactAdapter mAdapter;
    private TextView mContactTextView;
    public TextView ok_btn;
    public TextView cancel_btn;
    public EditText phone_text;
    public String number = "";
    public MessageCategory breakUpMessage;
    public String send_name;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mContactRecyclerView = (RecyclerView) view.findViewById(R.id.contact_recycler_view);
        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContactTextView = (TextView) view.findViewById(R.id.empty_view);
        breakUpMessage = (MessageCategory) getArguments().getSerializable("message_category");
        ContactGroupData m_ContactGroupData = new ContactGroupData();
        m_ContactGroupData.execute();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        instance = this;
    }

    public static ContactFragment getInstance() {
        return instance;
    }

    public void getContactDataFromPhone() {
        new ImportContactsAsync(getActivity(), new ImportContactsAsync.ICallback() {
            @Override
            public void mobileContacts(ArrayList<Contact> contactList) {
                ArrayList<Contact> listItem = contactList;
                List<ContactInfo> contactInfoList = new ArrayList<>();
                for (int k = 0; k < listItem.size(); k++) {
                    Contact temp_contact = listItem.get(k);
                    LinkedList<NumberContainer> number = temp_contact.getNumbers();
                    LinkedList<EmailContainer> email = temp_contact.getEmails();

                    ContactInfo data = new ContactInfo();
                    data.setmContact_id(String.valueOf(temp_contact.getId()));

                    List<String> phone_list = new ArrayList<>();
                    for (int i = 0; i < number.size(); i++) {
                        String phone_number = number.get(i).elementValue();
                        phone_list.add(phone_number);
                    }
                    if (temp_contact.getDisplaydName() == null || temp_contact.getDisplaydName().isEmpty()) {
                        data.setName(String.valueOf(number.get(0).elementValue()));
                    } else {
                        data.setName(temp_contact.getDisplaydName());
                    }
                    data.setPhones(phone_list);
                    List<String> email_list = new ArrayList<>();
                    for (int j = 0; j < email.size(); j++) {
                        String email_string = email.get(j).getEmail();
                        email_list.add(email_string);
                    }
                    data.setEmails(email_list);
                    contactInfoList.add(data);
                }
                all_contact = contactInfoList;
                updateUI1();
            }
        }).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        ContactGroupData m_ContactGroupData = new ContactGroupData();
        m_ContactGroupData.execute();
    }

    public void updateUI1() {
        contacts = new ArrayList<>();
        contacts = all_contact;
        Log.d("-----contact_count:", String.valueOf(contacts.size()));
        if (mAdapter == null) {
            mAdapter = new ContactAdapter(contacts);
            mContactRecyclerView.setAdapter(mAdapter);
            SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
            if (!sh.contains("name")) {
                Log.d("----key is exist", "name");
                showHelpDialogue();
            }
        } else {
            mAdapter.setContacts(contacts);
            mAdapter.notifyDataSetChanged();
        }
        if (contacts.size() == 0) {
            mContactRecyclerView.setVisibility(View.GONE);
            mContactTextView.setVisibility(View.VISIBLE);
        } else {
            mContactRecyclerView.setVisibility(View.VISIBLE);
            mContactTextView.setVisibility(View.GONE);
        }

    }

    private void showHelpDialogue() {
        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setMessage(R.string.help)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("name", "sms_contact");
                        myEdit.apply();
                    }

                })
                .show();
    }

    public class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {

        private List<ContactInfo> mContacts;
        private LinearLayout layout;
        private int position;

        public ContactAdapter(List<ContactInfo> contacts) {
            mContacts = contacts;
            setHasStableIds(true);
        }

        @Override
        public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_contact, parent, false);
            layout = (LinearLayout) view.findViewById(R.id.contact_item_layout);
            return new ContactHolder(view, this);
        }

        @Override
        public void onBindViewHolder(ContactHolder holder, int position) {
            ContactInfo contact = mContacts.get(position);
            String contact_string = "" + contact.getName().charAt(0);
            holder.bindContact(contact, position);
        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        public void setContacts(List<ContactInfo> contacts) {
            mContacts = contacts;
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

    public class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mNameTextView;
        private ImageView mPhotoView;
        private TextView mSectionTextView;
        private CheckBox mSolvedCheckBox;
        private ContactInfo mContact;
        private ContactAdapter mAdapter;
        int imageViewWidth = 0;
        int imageViewHeight = 0;
        public LinearLayout item_layout;
        public int select_pos = 0;

        public ContactHolder(View itemView, ContactAdapter adaptor) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.list_item_contact_name);
            item_layout = (LinearLayout) itemView.findViewById(R.id.contact_item_layout);

            itemView.setOnClickListener(this);
            mAdapter = adaptor;
        }

        public void bindContact(ContactInfo contact, int position) {
            mContact = contact;
            mNameTextView.setText(mContact.getName());
            select_pos = position;
        }

        @Override
        public void onClick(View v) {
            showConfirmSaveDialogue();
        }

        String regexStr = "^[0-9]*$";

        public String getPhoneNumber(String number) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < number.length(); i++) {
                if (number.substring(i, i + 1).matches(regexStr)) {
                    //str += number.substring(i, i + 1);
                    str.append(number.charAt(i));
                }
            }
            return str.toString();
        }

        private void showConfirmSaveDialogue() {
            new androidx.appcompat.app.AlertDialog.Builder(getActivity())
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setTitle("Block")
                    .setMessage(R.string.contact_send)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            number = "";
                            if (mContact.getPhones().size() > 0) {
                                number = getPhoneNumber(mContact.getPhones().get(0));
                                send_name = mContact.getName();
                                setDefaultPhoneApp();
                            } else {
                                Toast.makeText(getContext(), "This contact doesn't contain any phone number.", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            number = "";
//                            number = getPhoneNumber(mContact.getPhones().get(0));
//                            sendSms(mContact.getName());
                        }

                    })
                    .show();
        }

    }

    public void setDefaultSmsApp(String packageName) {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
        startActivityForResult(intent, 3);
    }

    private void setDefaultPhoneApp() {
        TelecomManager telecomManager = (TelecomManager) getActivity().getSystemService(TELECOM_SERVICE);
        if (!getActivity().getPackageName().equals(telecomManager.getDefaultDialerPackage())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                RoleManager rm = (RoleManager) getActivity().getSystemService(ROLE_SERVICE);
                if (rm.isRoleAvailable(RoleManager.ROLE_DIALER))
                    startActivityForResult(rm.createRequestRoleIntent(RoleManager.ROLE_DIALER), 3);
            } else {
                Intent intent = new Intent(ACTION_CHANGE_DEFAULT_DIALER).putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getActivity().getPackageName());
                startActivityForResult(intent, 3);
            }
        } else {
            PackageManager manager = getActivity().getPackageManager();
            ComponentName component = new ComponentName(getActivity().getPackageName(), "com.contact.unmatch.CallService");
            manager.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            onBlock(number);
            sendSms();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
//            ((MainActivity)getActivity()).service_status = resultCode;
            onBlock(number);
            sendSms();
        } else {
            Toast.makeText(getContext(), "You need to set Unmatch app as default phone app.", Toast.LENGTH_LONG).show();
        }
    }

    public void onBlock(String block_number) {
        /*TelecomManager telecomManager = getActivity().getSystemService(TelecomManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getActivity().startActivity(telecomManager.createManageBlockedNumbersIntent(), null);
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ContentValues values = new ContentValues();
            values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, block_number);

            Uri uri = getActivity().getContentResolver().insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI, values);
            if (uri != null) {
                Toast.makeText(getActivity().getApplicationContext(), block_number + " is blocked", Toast.LENGTH_SHORT).show();
            }
            PackageManager manager = getActivity().getPackageManager();
            ComponentName component = new ComponentName(getActivity().getPackageName(), "com.contact.unmatch.CallService");
//                manager.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            manager.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public String phoneInvalidation(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(getActivity());
        ;
        try {
            if (!number.startsWith("+")) {
                number = "+" + number;
            }
            Phonenumber.PhoneNumber NumberProto = phoneUtil.parse(number, "");
            String phone_number = phoneUtil.format(NumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
            Log.d("----phone_number:", phone_number);
            if (!phoneUtil.isValidNumber(NumberProto)) {
//                Log.d("----country_code:", "invalid");
                return null;
            } else {
//                String regionISO = phoneUtil.getRegionCodeForCountryCode(NumberProto.getCountryCode());       // get country code
//                Log.d("----country_code:", regionISO);
                return phoneUtil.format(NumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);       // convert phone_number as E164 format

            }

        } catch (NumberParseException e) {
            Log.d("----country_code:", e.toString());
            return null;
        }
    }

    public void sendSms() {
        String send_phoneNumber = phoneInvalidation(number);
        if (send_phoneNumber != null) {
            postSms(send_name, send_phoneNumber);
        } else {
            //Toast.makeText(getActivity(), "invalid number", Toast.LENGTH_SHORT).show();
            showErrorDialog("invalid number");
        }
    }

    public void postSms(String name, String sendNumber) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            obj.put("phoneNumber", sendNumber);
            obj.put("breakupMessageId", breakUpMessage.getMessageId());
            String url = HttpUtils.API_URL + "Exes";
            progressBar.setVisibility(View.VISIBLE);
            HttpUtils.post(getActivity(), Utils.getAccessToken(), url, obj, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("Success", "" + statusCode + " : " + responseString);
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(getActivity(), CloseActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("Failure", "" + statusCode + " : " + responseString);
                    if (statusCode == 401) {
                        HttpUtils.refreshtoken(new RefreshTokenListener() {
                            @Override
                            public void onSuccessRefreshToken() {
                                progressBar.setVisibility(View.GONE);
                                sendSms();
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
                        showErrorDialog(responseString.trim());
                        //Toast.makeText(getActivity(), responseString, responseString.length()).show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), R.string.message_server_error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.message_unknown_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorDialog(String error) {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Number")
                .setMessage(error)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}