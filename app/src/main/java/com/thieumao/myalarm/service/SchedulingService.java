package com.thieumao.myalarm.service;

import android.app.IntentService;
import android.content.Intent;

import com.thieumao.myalarm.data.Constants;
import com.thieumao.myalarm.utility.AlarmUtils;

public class SchedulingService extends IntentService {
    public SchedulingService() {
        super(SchedulingService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int id = intent.getIntExtra(Constants.OBJECT_ID, Constants.DEFAULT_INTENT_VALUE);
        if (id > Constants.DEFAULT_INTENT_VALUE) {
            AlarmUtils.startAlarm(this, id);
        }
    }
}
