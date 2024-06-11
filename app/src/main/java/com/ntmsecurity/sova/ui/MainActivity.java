package com.ntmsecurity.sova.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import org.unifiedpush.android.connector.UnifiedPush;

import java.util.ArrayList;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ntmsecurity.sova.R;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.WhiteList;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;
import com.ntmsecurity.sova.data.io.json.JSONWhiteList;
import com.ntmsecurity.sova.ui.settings.SettingsActivity;
import com.ntmsecurity.sova.ui.settings.WhiteListActivity;
import com.ntmsecurity.sova.utils.Logger;
import com.ntmsecurity.sova.utils.Notifications;
import com.ntmsecurity.sova.utils.Permission;
import host.stjin.expandablecardview.ExpandableCardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewSovaCommandName;
    private TextView textViewWhiteListCount;
    private TextView textViewCORE;
    private TextView textViewGPS;
    private TextView textViewDND;
    private TextView textViewDeviceAdmin;
    private TextView textViewWriteSecureSettings;
    private TextView textViewOverlay;
    private TextView textViewNotification;
    private TextView textViewCamera;
    private TextView textViewBatteryOptimization;
    private TextView textViewServerServiceEnabled;
    private TextView textViewServerRegistered;
    private TextView textViewPush;
    private Button buttonOpenWhitelist;
    private ExpandableCardView expandableCardViewPermissions;
    private BottomNavigationView bottomNavigationView;

    private WhiteList whiteList;
    private Settings Settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IO.context = this;
        Logger.init(Thread.currentThread(), this);
        Notifications.init(this, false);
        whiteList = JSONFactory.convertJSONWhiteList(IO.read(JSONWhiteList.class, IO.whiteListFileName));
        Settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));
        Permission.initValues(this);
        if(((Integer) Settings.get(Settings.SET_APP_CRASHED_LOG_ENTRY)) != -1){
            Intent crash = new Intent(this, CrashedActivity.class);
            startActivity(crash);
        }
        Settings.updateSettings();
        if (!Settings.isIntroductionPassed() || !Permission.CORE) {
            Intent introductionIntent = new Intent(this, IntroductionActivity.class);
            startActivity(introductionIntent);
        }
        reloadViews();
        updateViews();

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        return true;
                    case R.id.availableActions:
                        startActivity(new Intent(getApplicationContext(), AvailableActions.class));
                        finish();
                        //overridePendingTransition(0, 0);
                        return true;
                    case R.id.trySendSMS:
                        startActivity(new Intent(getApplicationContext(), TrySendSMS.class));
                        finish();
                        //overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    public void reloadViews() {
        textViewSovaCommandName = findViewById(R.id.textViewSovaCommandName);
        textViewWhiteListCount = findViewById(R.id.textViewWhiteListCount);
        textViewCORE = findViewById(R.id.textViewCORE);
        textViewGPS = findViewById(R.id.textViewGPS);
        textViewDND = findViewById(R.id.textViewDND);
        textViewDeviceAdmin = findViewById(R.id.textViewDeviceAdmin);
        textViewWriteSecureSettings = findViewById(R.id.textViewWriteSecureSettings);
        textViewOverlay = findViewById(R.id.textViewOverlay);
        textViewNotification = findViewById(R.id.textViewNotification);
        buttonOpenWhitelist = findViewById(R.id.buttonOpenWhiteList);
        buttonOpenWhitelist.setOnClickListener(this);
        textViewServerServiceEnabled = findViewById(R.id.textViewServerEnabled);
        textViewServerRegistered = findViewById(R.id.textViewRegisteredOnServer);
        textViewPush = findViewById(R.id.textViewPushAvailable);
        textViewCamera = findViewById(R.id.textViewCamera);
        textViewBatteryOptimization = findViewById(R.id.textviewBatteryOptimization);
        expandableCardViewPermissions = findViewById(R.id.expandableCardViewPermissions);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    public void updateViews() {
        textViewSovaCommandName.setText((String) Settings.get(Settings.SET_Sova_COMMAND));
        textViewWhiteListCount.setText(Integer.valueOf(whiteList.size()).toString());


        int colorEnabled;
        int colorDisabled;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorEnabled = getColor(R.color.colorEnabled);
            colorDisabled = getColor(R.color.colorDisabled);
        }else {
            colorEnabled = getResources().getColor(R.color.colorEnabled);
            colorDisabled = getResources().getColor(R.color.colorDisabled);
        }

        if(whiteList.size() > 0){
            textViewWhiteListCount.setTextColor(colorEnabled);
        }else{
            textViewWhiteListCount.setTextColor(colorDisabled);
        }

        if (Permission.CORE) {
            textViewCORE.setText(getString(R.string.Enabled));
            textViewCORE.setTextColor(colorEnabled);
        } else {
            textViewCORE.setText(getString(R.string.Disabled));
            textViewCORE.setTextColor(colorDisabled);
        }
        if (Permission.GPS) {
            textViewGPS.setText(getString(R.string.Enabled));
            textViewGPS.setTextColor(colorEnabled);
        } else {
            textViewGPS.setText(getString(R.string.Disabled));
            textViewGPS.setTextColor(colorDisabled);
        }
        if (Permission.DND) {
            textViewDND.setText(getString(R.string.Enabled));
            textViewDND.setTextColor(colorEnabled);
        } else {
            textViewDND.setText(getString(R.string.Disabled));
            textViewDND.setTextColor(colorDisabled);
        }
        if (Permission.DEVICE_ADMIN) {
            textViewDeviceAdmin.setText(getString(R.string.Enabled));
            textViewDeviceAdmin.setTextColor(colorEnabled);
        } else {
            textViewDeviceAdmin.setText(getString(R.string.Disabled));
            textViewDeviceAdmin.setTextColor(colorDisabled);
        }
        if (Permission.WRITE_SECURE_SETTINGS) {
            textViewWriteSecureSettings.setText(getString(R.string.Enabled));
            textViewWriteSecureSettings.setTextColor(colorEnabled);
        } else {
            textViewWriteSecureSettings.setText(getString(R.string.Disabled));
            textViewWriteSecureSettings.setTextColor(colorDisabled);
        }
        if (Permission.OVERLAY) {
            textViewOverlay.setText(getString(R.string.Enabled));
            textViewOverlay.setTextColor(colorEnabled);
        } else {
            textViewOverlay.setText(getString(R.string.Disabled));
            textViewOverlay.setTextColor(colorDisabled);
        }
        if(Permission.NOTIFICATION){
            textViewNotification.setText(getString(R.string.Enabled));
            textViewNotification.setTextColor(colorEnabled);
        } else {
            textViewNotification.setText(getString(R.string.Disabled));
            textViewNotification.setTextColor(colorDisabled);
        }
        if(Permission.CAMERA){
            textViewCamera.setText(getString(R.string.Enabled));
            textViewCamera.setTextColor(colorEnabled);
        } else {
            textViewCamera.setText(getString(R.string.Disabled));
            textViewCamera.setTextColor(colorDisabled);
        }
        if(Permission.BATTERY_OPTIMIZATION){
            textViewBatteryOptimization.setText(R.string.Enabled);
            textViewBatteryOptimization.setTextColor(colorEnabled);
        } else {
            textViewBatteryOptimization.setText(R.string.Disabled);
            textViewBatteryOptimization.setTextColor(colorDisabled);
        }
        expandableCardViewPermissions.setTitle(-1, getString(R.string.Granted) + " " + Permission.ENABLED_PERMISSIONS + "/" + Permission.AVAILABLE_PERMISSIONS);

        if((Boolean) Settings.get(Settings.SET_SovaSERVER_UPLOAD_SERVICE)){
            textViewServerServiceEnabled.setText(getString(R.string.Enabled));
            textViewServerServiceEnabled.setTextColor(colorEnabled);
        }else{
            textViewServerServiceEnabled.setText(getString(R.string.Disabled));
            textViewServerServiceEnabled.setTextColor(colorDisabled);
        }

        if(!((String) Settings.get(Settings.SET_SovaSERVER_ID)).isEmpty()){
            textViewServerRegistered.setText(getString(R.string.Enabled));
            textViewServerRegistered.setTextColor(colorEnabled);
        }else{
            textViewServerRegistered.setText(getString(R.string.Disabled));
            textViewServerRegistered.setTextColor(colorDisabled);
        }

        if(UnifiedPush.getDistributors(this, new ArrayList<String>()).size() > 0){
            textViewPush.setText(R.string.Available);
            textViewPush.setTextColor(colorEnabled);
        }else{
            textViewPush.setTextColor(colorDisabled);
            textViewPush.setText(R.string.NOT_AVAILABLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuItemSettings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Permission.initValues(this);
        whiteList = JSONFactory.convertJSONWhiteList(IO.read(JSONWhiteList.class, IO.whiteListFileName));
        Settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));
        if (!Settings.isIntroductionPassed() || !Permission.CORE) {
            Intent introductionIntent = new Intent(this, IntroductionActivity.class);
            startActivity(introductionIntent);
        }
        updateViews();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonOpenWhitelist){
            Intent whiteListActivity = new Intent(this, WhiteListActivity.class);
            startActivity(whiteListActivity);
        }
    }
}