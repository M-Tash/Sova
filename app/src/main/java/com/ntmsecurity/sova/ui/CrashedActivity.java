package com.ntmsecurity.sova.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ntmsecurity.sova.R;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.LogData;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONLog;
import com.ntmsecurity.sova.data.io.json.JSONMap;

public class CrashedActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String CRASH_LOG = "log";

    private TextView textViewCrashLog;
    private Button buttonSendLog;

    private String crashLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crashed);
        IO.context = this;
        Settings Settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));
        LogData log = JSONFactory.convertJSONLog(IO.read(JSONLog.class, IO.logFileName));
        crashLog = log.get((Integer) Settings.get(Settings.SET_APP_CRASHED_LOG_ENTRY)).getText();
        Settings.set(Settings.SET_APP_CRASHED_LOG_ENTRY, -1);

        textViewCrashLog = findViewById(R.id.textViewCrash);
        textViewCrashLog.setText(crashLog);

        buttonSendLog = findViewById(R.id.buttonSendLog);
        buttonSendLog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType ("plain/text");
        intent.putExtra (Intent.EXTRA_EMAIL, new String[] {"eng.mohamedibrahimnabih@gmail.com"});
        intent.putExtra (Intent.EXTRA_SUBJECT, "CrashLog");
        intent.putExtra (Intent.EXTRA_TEXT, crashLog);
        startActivity (intent);
    }
}