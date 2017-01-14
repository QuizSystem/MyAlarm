package com.thieumao.myalarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thieumao.myalarm.Alarm;
import com.thieumao.myalarm.R;
import com.thieumao.myalarm.utility.Constants;
import com.thieumao.myalarm.AlarmRepository;
import com.thieumao.myalarm.listener.OnTimeSetPickerListener;
import com.thieumao.myalarm.TimePickerFragment;
import com.thieumao.myalarm.utility.AlarmUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import io.realm.Realm;

public class AlarmDetailActivity extends BaseActivity implements View.OnClickListener, OnTimeSetPickerListener {
    private TextView mTextViewAlarmTime;
    private Button mButtonSaveNewAlarm, mButtonSaveAlarm, mButtonDeleteAlarm;
    private Realm mRealm;
    private Alarm mAlarm;
    private TimePickerFragment mTimePickerFragment;
    private String mTimeDefault;
    private int mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);
        initViews();
        handleViewsOnClick();
        loadData();
        setDataToViews();
        onChangeRotate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.TIME_ALARM_ON_ROTATE_CHANGE,
                mTextViewAlarmTime.getText().toString());
    }

    private void onChangeRotate(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mTextViewAlarmTime.setText(savedInstanceState.getString(
                    Constants.TIME_SLEEP_ON_ROTATE_CHANGE, mTimeDefault));
        if (mTimePickerFragment == null)
            mTimePickerFragment =
                    TimePickerFragment.newInstance(this, mTextViewAlarmTime.getText().toString());
    }

    private void initViews() {
        getSupportActionBar().setTitle(R.string.set_alarm);
        mTextViewAlarmTime = (TextView) findViewById(R.id.text_view_alarm_time);
        mButtonSaveNewAlarm = (Button) findViewById(R.id.button_save_new_alarm);
        mButtonSaveAlarm = (Button) findViewById(R.id.button_save_alarm);
        mButtonDeleteAlarm = (Button) findViewById(R.id.button_delete_alarm);
    }

    private void handleViewsOnClick() {
        mTextViewAlarmTime.setOnClickListener(this);
        mButtonSaveNewAlarm.setOnClickListener(this);
        mButtonSaveAlarm.setOnClickListener(this);
        mButtonDeleteAlarm.setOnClickListener(this);
    }

    private void loadData() {
        mRealm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        mId = intent.getIntExtra(Constants.OBJECT_ID, Constants.DEFAULT_INTENT_VALUE);
        if (mId == Constants.DEFAULT_INTENT_VALUE) {
            mButtonSaveNewAlarm.setVisibility(View.VISIBLE);
            createNewAlarm();
        } else {
            mButtonSaveAlarm.setVisibility(View.VISIBLE);
            mButtonDeleteAlarm.setVisibility(View.VISIBLE);
            mAlarm = AlarmRepository.getAlarmById(mId);
        }
        mRealm.beginTransaction();
        mRealm.commitTransaction();
    }

    private void setDataToViews() {
        mTimeDefault =
                new SimpleDateFormat(Constants.ALARM_TIME_FORMAT).format(new Date(mAlarm.getTime()));
        mTextViewAlarmTime.setText(mTimeDefault);
    }

    private void createNewAlarm() {
        mRealm.beginTransaction();
        mAlarm = mRealm.createObject(Alarm.class);
        mAlarm.setId(AlarmRepository.getNextId());
        mAlarm.setTime(Calendar.getInstance().getTimeInMillis());
        mAlarm.setEnabled(true);
        mRealm.commitTransaction();
    }

    private long convertStringToTimeLong(String timeString) {
        DateFormat formatter = new SimpleDateFormat(Constants.ALARM_TIME_FORMAT);
        Date date;
        try {
            date = formatter.parse(timeString);
        } catch (ParseException e) {
            date = new Date();
        }
        return date.getTime();
    }

    private void saveAlarm() {
        mRealm.beginTransaction();
        mAlarm.setTime(convertStringToTimeLong(mTextViewAlarmTime.getText().toString()));
        mRealm.commitTransaction();
        AlarmRepository.updateAlarm(mAlarm);
        AlarmUtils.setupAlarm(this, mAlarm);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_alarm_time:
                mTimePickerFragment =
                        TimePickerFragment.newInstance(this, mTextViewAlarmTime.getText().toString());
                mTimePickerFragment.show(getSupportFragmentManager(), Constants.TIME_PICKER);
                break;
            case R.id.button_save_new_alarm:
            case R.id.button_save_alarm:
                saveAlarm();
                break;
            case R.id.button_delete_alarm:
                AlarmRepository.deleteAlarm(mAlarm);
                finish();
                break;
        }
    }

    @Override
    public void onTimeSetPicker(int hourOfDay, int minute) {
        mTextViewAlarmTime.setText(TimePickerFragment.getFormatTime(this, hourOfDay, minute));
    }

    @Override
    public void onBackPressed() {
        if (mId == Constants.DEFAULT_INTENT_VALUE) AlarmRepository.deleteAlarm(mAlarm);
        finish();
    }
}
