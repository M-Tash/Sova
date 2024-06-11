package com.ntmsecurity.sova.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.ntmsecurity.sova.R;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;

public class OpenCellIdActivity extends AppCompatActivity implements TextWatcher {

    private Settings Settings;

    private EditText editTextOpenCellIdKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cell_id);

        Settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));

        editTextOpenCellIdKey = findViewById(R.id.editTextOpenCellIDAPIKey);
        editTextOpenCellIdKey.setText((String) Settings.get(Settings.SET_OPENCELLID_API_KEY));
        editTextOpenCellIdKey.addTextChangedListener(this);

        Button buttonOK = findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(v -> finish());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable edited) {
        if (edited == editTextOpenCellIdKey.getText()) {
            Settings.set(Settings.SET_OPENCELLID_API_KEY, edited.toString());
        }
    }
}