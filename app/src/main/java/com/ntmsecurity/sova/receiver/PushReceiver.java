package com.ntmsecurity.sova.receiver;

import android.content.Context;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.unifiedpush.android.connector.MessagingReceiver;
import org.unifiedpush.android.connector.UnifiedPush;

import java.util.ArrayList;

import com.ntmsecurity.sova.net.DataHandler;
import com.ntmsecurity.sova.services.SovaServerCommandService;


public class PushReceiver extends MessagingReceiver {

    public PushReceiver() {
        super();
    }

    @Override
    public void onMessage(@NonNull Context context, @NonNull byte[] message, @NonNull String instance) {
        SovaServerCommandService.scheduleJobNow(
                context);
    }

    @Override
    public void onNewEndpoint(@Nullable Context context, @NotNull String s, @NotNull String s1) {

        DataHandler dataHandler = new DataHandler(context);

        JSONObject dataPackage = new JSONObject();
        try {
            dataPackage.put("IDT", "");
            dataPackage.put("Data", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataHandler.run(DataHandler.PUSH, dataPackage, null );

    }

    @Override
    public void onRegistrationFailed(@Nullable Context context, @NotNull String s) {

    }

    @Override
    public void onUnregistered(@Nullable Context context, @NotNull String s) {

    }

    public static void Register(Context c){
        if(UnifiedPush.getDistributors(c, new ArrayList<>()).size() > 0){
            UnifiedPush.registerAppWithDialog(c, "", "", new ArrayList<>(), "");
            new PushReceiver();
        }
    }
}
