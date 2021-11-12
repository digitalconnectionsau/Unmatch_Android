package com.contact.unmatch;

import android.telecom.Call;
import android.telecom.InCallService;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
    }
}
