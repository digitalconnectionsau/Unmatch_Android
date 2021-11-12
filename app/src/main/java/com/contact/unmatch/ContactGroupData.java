package com.contact.unmatch;

import android.os.AsyncTask;

public class ContactGroupData extends AsyncTask<Object, Integer, Void>  {

    @Override
    protected Void doInBackground(Object... objects) {
        ContactFragment.getInstance().getContactDataFromPhone();
        return null;
    }
}
