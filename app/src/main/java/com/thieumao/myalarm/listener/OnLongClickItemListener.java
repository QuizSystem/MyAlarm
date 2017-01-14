package com.thieumao.myalarm.listener;

import android.view.View;

import com.thieumao.myalarm.AlarmRecyclerViewAdapter;

public interface OnLongClickItemListener {
    void onLongClickItem(View view, AlarmRecyclerViewAdapter.AlarmViewHolder holder, int position);
}
