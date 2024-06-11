package com.ntmsecurity.sova.ui;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ntmsecurity.sova.R;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;
import com.ntmsecurity.sova.sender.FooSender;
import com.ntmsecurity.sova.sender.SMS;
import com.ntmsecurity.sova.sender.Sender;

public class LockScreenMessage extends AppCompatActivity {

    public static final String SENDER = "sender";
    public static final String SENDER_TYPE = "type";
    public static final String CUSTOM_TEXT = "ctext";
    private Sender sender;

    private TextView tvLockScreenMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen_message);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        Bundle bundle = getIntent().getExtras();
        switch(bundle.getString(SENDER_TYPE)){
            case SMS.TYPE:
                    sender = new SMS(bundle.getString(SENDER));
                break;
            default:
                sender = new FooSender();
        }
        Settings Settings;
        IO.context = this;
        Settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));
        tvLockScreenMessage = findViewById(R.id.textViewLockScreenMessage);
        if (bundle.containsKey(CUSTOM_TEXT)) {
            tvLockScreenMessage.setText(bundle.getString(CUSTOM_TEXT));
        } else {
            tvLockScreenMessage.setText((String) Settings.get(Settings.SET_LOCKSCREEN_MESSAGE));
        }
    }

    @Override
    protected void onPause() {
        sender.sendNow(getString(R.string.LockScreen_Usage_detectd));
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        sender.sendNow(getString(R.string.LockScreen_Backbutton_pressed));
        finish();
    }



}