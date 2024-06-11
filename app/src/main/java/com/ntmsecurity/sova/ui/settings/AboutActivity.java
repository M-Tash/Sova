package com.ntmsecurity.sova.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ntmsecurity.sova.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewSovaVersion;
    private Button buttonHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textViewSovaVersion = findViewById(R.id.textViewSovaVersion);

        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getPackageName(), 0);
            textViewSovaVersion.setText(info.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            textViewSovaVersion.setText(getString(R.string.Error));
        }

        buttonHelp = findViewById(R.id.buttonHelp);
        buttonHelp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent helpIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("###"));
        startActivity(helpIntent);
    }
}