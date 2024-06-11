package com.ntmsecurity.sova.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.ntmsecurity.sova.R;
import com.ntmsecurity.sova.data.Keys;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;
import com.ntmsecurity.sova.receiver.PushReceiver;
import com.ntmsecurity.sova.services.SovaServerService;
import com.ntmsecurity.sova.utils.CypherUtils;

public class SovaServerActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener {

    private Settings settings;

    private CheckBox checkBoxSovaServer;
    private CheckBox checkBoxSovaServerAutoUpload;
    private EditText editTextSovaServerURL;
    private EditText editTextSovaServerUpdateTime;
    private TextView textViewSovaServerID;
    private Button registerButton;
    private Button deleteButton;
    private CheckBox checkBoxSovaServerGPS;
    private CheckBox checkBoxSovaServerCell;
    private CheckBox checkBoxLowBat;

    private int colorEnabled;
    private int colorDisabled;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sova_server);

        settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));
        this.context = this;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorEnabled = getColor(R.color.colorEnabled);
            colorDisabled = getColor(R.color.colorDisabled);
        } else {
            colorEnabled = getResources().getColor(R.color.colorEnabled);
            colorDisabled = getResources().getColor(R.color.colorDisabled);
        }


        checkBoxSovaServer = findViewById(R.id.checkBoxSovaServer);
        checkBoxSovaServer.setChecked((Boolean) settings.get(Settings.SET_SovaSERVER_UPLOAD_SERVICE));
        checkBoxSovaServer.setOnCheckedChangeListener(this);
        if(((String) settings.get(Settings.SET_SovaSERVER_ID)).isEmpty()){
           checkBoxSovaServer.setEnabled(false);
        }

        checkBoxSovaServerAutoUpload = findViewById(R.id.checkBoxSovaServerAutoUpload);
        checkBoxSovaServerAutoUpload.setChecked((Boolean) settings.get(Settings.SET_SovaSERVER_AUTO_UPLOAD));
        checkBoxSovaServerAutoUpload.setOnCheckedChangeListener(this);

        editTextSovaServerURL = findViewById(R.id.editTextSovaServerUrl);
        editTextSovaServerURL.setText((String) settings.get(Settings.SET_SovaSERVER_URL));
        editTextSovaServerURL.addTextChangedListener(this);

        editTextSovaServerUpdateTime = findViewById(R.id.editTextSovaServerUpdateTime);
        editTextSovaServerUpdateTime.setText(((Integer) settings.get(Settings.SET_SovaSERVER_UPDATE_TIME)).toString());
        editTextSovaServerUpdateTime.addTextChangedListener(this);

        textViewSovaServerID = findViewById(R.id.textViewID);

        deleteButton = findViewById(R.id.buttonDeleteData);
        deleteButton.setOnClickListener(this);

        if (!((String) settings.get(Settings.SET_SovaSERVER_ID)).isEmpty()) {
            textViewSovaServerID.setText((String) settings.get(Settings.SET_SovaSERVER_ID));
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setEnabled(true);
        }

        registerButton = findViewById(R.id.buttonRegisterOnServer);
        Boolean passwordSet = (Boolean) settings.get(Settings.SET_SovaSERVER_PASSWORD_SET);
        if (passwordSet) {
            registerButton.setBackgroundColor(colorEnabled);
        } else {
            registerButton.setBackgroundColor(colorDisabled);
        }
        if(((String) settings.get(Settings.SET_SovaSERVER_URL)).isEmpty()){
            registerButton.setEnabled(false);
        }
        registerButton.setOnClickListener(this);

        if(!(Boolean) settings.get(Settings.SET_FIRST_TIME_Sova_SERVER)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.Settings_SovaServer))
                    .setMessage(this.getString(R.string.Alert_First_time_Sovaserver))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            settings.set(Settings.SET_FIRST_TIME_Sova_SERVER, true);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }

        checkBoxSovaServerGPS = findViewById(R.id.checkBoxSovaServerGPS);
        checkBoxSovaServerCell = findViewById(R.id.checkBoxSovaServerCell);
        switch((Integer)settings.get(Settings.SET_SovaSERVER_LOCATION_TYPE)){
            case 0:
                checkBoxSovaServerGPS.setChecked(true);
                break;
            case 1:
                checkBoxSovaServerCell.setChecked(true);
                break;
            case 2:
                checkBoxSovaServerGPS.setChecked(true);
                checkBoxSovaServerCell.setChecked(true);
        }
        checkBoxSovaServerGPS.setOnCheckedChangeListener(this);
        checkBoxSovaServerCell.setOnCheckedChangeListener(this);

        checkBoxLowBat = findViewById(R.id.checkBoxSovaServerLowBatUpload);
        if((Boolean)settings.get(Settings.SET_Sova_LOW_BAT_SEND)){
            checkBoxLowBat.setChecked(true);
        }else{
            checkBoxLowBat.setChecked(false);
        }
        checkBoxLowBat.setOnCheckedChangeListener(this);

        Button buttonOK = findViewById(R.id.buttonSave);
        buttonOK.setOnClickListener(v -> finish());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkBoxSovaServer) {
            settings.setNow(Settings.SET_SovaSERVER_UPLOAD_SERVICE, isChecked);
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SovaServerService.scheduleJob(this, 0);
                    PushReceiver.Register(context);

                }
            }else{
                SovaServerService.cancelAll(this);
            }
        }else if(buttonView == checkBoxSovaServerAutoUpload){
            settings.set(Settings.SET_SovaSERVER_AUTO_UPLOAD, isChecked);
        }else if(buttonView == checkBoxSovaServerCell || buttonView == checkBoxSovaServerGPS){
            if(checkBoxSovaServerGPS.isChecked() && checkBoxSovaServerCell.isChecked()){
                settings.set(Settings.SET_SovaSERVER_LOCATION_TYPE, 2);
            }else if(checkBoxSovaServerGPS.isChecked()){
                settings.set(Settings.SET_SovaSERVER_LOCATION_TYPE, 0);
            }else if(checkBoxSovaServerCell.isChecked()){
                settings.set(Settings.SET_SovaSERVER_LOCATION_TYPE, 1);
            }else{
                settings.set(Settings.SET_SovaSERVER_LOCATION_TYPE, 0);
            }
        }else if(buttonView == checkBoxLowBat){
            settings.set(Settings.SET_Sova_LOW_BAT_SEND, isChecked);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable edited) {
        if (edited == editTextSovaServerURL.getText()) {
            settings.set(Settings.SET_SovaSERVER_URL, edited.toString());
            if(edited.toString().isEmpty()){
                registerButton.setEnabled(false);
            }else{
                registerButton.setEnabled(true);
            }
        } else if (edited == editTextSovaServerUpdateTime.getText()) {
            if (edited.toString().isEmpty()) {
                settings.set(Settings.SET_SovaSERVER_UPDATE_TIME, 60);
            } else {
                settings.set(Settings.SET_SovaSERVER_UPDATE_TIME, Integer.parseInt(editTextSovaServerUpdateTime.getText().toString()));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == registerButton) {
            WebView webView = new WebView(context);
            webView.loadUrl(editTextSovaServerURL.getText().toString()+"/ds.html");

            final AlertDialog.Builder pinAlert = new AlertDialog.Builder(this);
            pinAlert.setTitle(getString(R.string.SovaConfig_Alert_Password));
            pinAlert.setMessage(getString(R.string.Settings_Enter_Password));
            final EditText input = new EditText(this);
            input.setPadding(50, 15, 50, 15);
            input.setTransformationMethod(new PasswordTransformationMethod());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                input.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E1E3E5")));
                input.setBackgroundColor(Color.parseColor("#E1E3E5"));
            }
            pinAlert.setIcon(android.R.drawable.ic_lock_lock);
            pinAlert.setView(input);
            pinAlert.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String text = input.getText().toString();
                    if (!text.isEmpty()) {
                        Keys keys = CypherUtils.genKeys(text);
                        settings.setKeys(keys);
                        String hashedPW = CypherUtils.hashWithPKBDF2(text);
                        settings.set(Settings.SET_Sova_CRYPT_HPW, hashedPW);
                        settings.setNow(Settings.SET_SovaSERVER_PASSWORD_SET, true);
                        SovaServerService.registerOnServer(context, (String) settings.get(Settings.SET_SovaSERVER_URL), keys.getEncryptedPrivateKey(), keys.getBase64PublicKey(), hashedPW);
                        finish();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                startActivity(getIntent());
                            }
                        }, 500);
                    }
                    else if(text.isEmpty()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.Toast_Empty_SovaCommand),Toast.LENGTH_SHORT).show();
                    }
                }
            });

            AlertDialog.Builder privacyPolicy = new AlertDialog.Builder(context);
            privacyPolicy.setTitle(getString(R.string.Settings_SovaServer_Alert_PrivacyPolicy_Title))
                    .setMessage("XXXXXXXXXXXXX")
                    .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pinAlert.show();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        }else if(v == deleteButton){
            AlertDialog.Builder privacyPolicy = new AlertDialog.Builder(context);
            privacyPolicy.setTitle(getString(R.string.Settings_SovaServer_Alert_DeleteData))
                    .setMessage(R.string.Settings_SovaServer_Alert_DeleteData_Desc)
                    .setPositiveButton(getString(R.string.Ok), new DialogClickListenerForUnregistration(this))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        }
    }

    private class DialogClickListenerForUnregistration implements DialogInterface.OnClickListener{

        private Context context;

        public DialogClickListenerForUnregistration(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            SovaServerService.unregisterOnServer(context);
            finish();
            startActivity(getIntent());
        }
    }

}