package com.ntmsecurity.sova.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Ringtone;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Timer;

import com.ntmsecurity.sova.R;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;
import com.ntmsecurity.sova.logic.command.helper.Ringer;
import com.ntmsecurity.sova.tasks.RingerTimerTask;

public class RingerActivity extends AppCompatActivity implements View.OnClickListener {

    public static String RING_DURATION = "rduration";

    private RingerTimerTask ringerTask;
    private Button buttonStopRinging;

    private Settings Settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle bundle = getIntent().getExtras();

        IO.context = this;
        Settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));

        Ringtone ringtone = Ringer.getRingtone(this, (String) Settings.get(Settings.SET_RINGER_TONE));

        Timer t = new Timer();
        ringerTask = new RingerTimerTask(t, ringtone, this);
        t.schedule(ringerTask, 0, bundle.getInt(RING_DURATION) * 100);
        ringtone.play();

        buttonStopRinging = findViewById(R.id.buttonStopRinging);
        buttonStopRinging.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v == buttonStopRinging){
            ringerTask.stop();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ringerTask.stop();
        finish();
    }
}