package com.ntmsecurity.sova.ui.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.ntmsecurity.sova.R;

public class SettingsViewAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<String> settingsEntries;

    public SettingsViewAdapter(Context context, List<String> settingsEntries) {
        inflater = (LayoutInflater.from(context));
        this.settingsEntries = settingsEntries;

    }

    @Override
    public int getCount() {
        return settingsEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.settings_item, null);
        TextView name = view.findViewById(R.id.textViewSettingsTitle);
        name.setText(settingsEntries.get(position));
        return view;
    }
}
