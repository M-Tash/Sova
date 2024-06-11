package com.ntmsecurity.sova.ui.settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.ntmsecurity.sova.R;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;
import com.ntmsecurity.sova.utils.CypherUtils;

public class SovaConfigActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener {

    private Settings settings;

    private CheckBox checkBoxDeviceWipe;
    private CheckBox checkBoxAccessViaPin;
    private Button buttonEnterPin;
    private Button buttonSelectRingtone;
    private EditText editTextLockScreenMessage;
    private EditText editTextSovaCommand;

    int colorEnabled;
    int colorDisabled;

    private int REQUEST_CODE_RINGTONE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sova_config);

        settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));

        checkBoxDeviceWipe = findViewById(R.id.checkBoxWipeData);
        checkBoxDeviceWipe.setChecked((Boolean) settings.get(Settings.SET_WIPE_ENABLED));
        checkBoxDeviceWipe.setOnCheckedChangeListener(this);

        checkBoxAccessViaPin = findViewById(R.id.checkBoxSovaviaPin);
        checkBoxAccessViaPin.setChecked((Boolean) settings.get(Settings.SET_ACCESS_VIA_PIN));
        checkBoxAccessViaPin.setOnCheckedChangeListener(this);

        editTextLockScreenMessage = findViewById(R.id.editTextTextLockScreenMessage);
        editTextLockScreenMessage.setText((String) settings.get(Settings.SET_LOCKSCREEN_MESSAGE));
        editTextLockScreenMessage.addTextChangedListener(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorEnabled = getColor(R.color.colorEnabled);
            colorDisabled = getColor(R.color.colorDisabled);
        }else {
            colorEnabled = getResources().getColor(R.color.colorEnabled);
            colorDisabled = getResources().getColor(R.color.colorDisabled);
        }

        buttonEnterPin = findViewById(R.id.buttonEnterPin);
        if(settings.get(Settings.SET_PIN).equals("")){
            buttonEnterPin.setBackgroundColor(colorDisabled);
        }else{
            buttonEnterPin.setBackgroundColor(colorEnabled);
        }
        buttonEnterPin.setOnClickListener(this);

        buttonSelectRingtone = findViewById(R.id.buttonSelectRingTone);
        buttonSelectRingtone.setOnClickListener(this);

        editTextSovaCommand = findViewById(R.id.editTextSovaCommand);
        editTextSovaCommand.setText((String) settings.get(Settings.SET_Sova_COMMAND));
        editTextSovaCommand.addTextChangedListener(this);

        Button buttonOK = findViewById(R.id.buttonSave);
        buttonOK.setOnClickListener(v -> finish());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkBoxDeviceWipe) {
            settings.set(Settings.SET_WIPE_ENABLED, isChecked);
        } else if (buttonView == checkBoxAccessViaPin) {
            settings.set(Settings.SET_ACCESS_VIA_PIN, isChecked);
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
        if (edited == editTextLockScreenMessage.getText()) {
            settings.set(Settings.SET_LOCKSCREEN_MESSAGE, edited.toString());
        } else if (edited == editTextSovaCommand.getText()) {
            if (edited.toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.Toast_Empty_SovaCommand), Toast.LENGTH_LONG).show();
                settings.set(Settings.SET_Sova_COMMAND, "sova");
            } else {
                settings.set(Settings.SET_Sova_COMMAND, edited.toString().toLowerCase());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonEnterPin) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getString(R.string.SovaConfig_Alert_Pin));
            alert.setMessage(getString(R.string.Settings_Enter_Pin));

            // Create the EditText object and set padding
            final EditText input = new EditText(this);
            input.setPadding(50, 15, 50, 15);
            input.setTransformationMethod(new PasswordTransformationMethod());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                input.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E1E3E5")));
                input.setBackgroundColor(Color.parseColor("#E1E3E5"));
            }
            alert.setIcon(android.R.drawable.ic_lock_lock);
            // Add the EditText to the AlertDialog view
            alert.setView(input);
            alert.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String text = input.getText().toString();
                    if (!text.isEmpty()) {
                        settings.set(Settings.SET_PIN, CypherUtils.hashPassword(text));
                        buttonEnterPin.setBackgroundColor(colorEnabled);
                        buttonEnterPin.setText("PIN IS ACTIVATED");
                    }
                    else if(text.isEmpty()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.Toast_Empty_SovaCommand),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alert.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alert.show();
        }else if(v == buttonSelectRingtone){
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.Settings_Select_Ringtone));
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false); // Disable silent option
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse((String) settings.get(Settings.SET_RINGER_TONE)));
            this.startActivityForResult(intent, REQUEST_CODE_RINGTONE);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_RINGTONE) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            settings.set(Settings.SET_RINGER_TONE, uri.toString());
        }
    }

}