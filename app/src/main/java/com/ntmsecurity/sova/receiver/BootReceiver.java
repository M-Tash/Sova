package com.ntmsecurity.sova.receiver;

import android.content.Context;
import android.content.Intent;


import com.ntmsecurity.sova.data.ConfigSMSRec;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.services.SovaServerService;
import com.ntmsecurity.sova.utils.Logger;

public class BootReceiver extends SuperReceiver{

    public static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        init(context);
        if (intent.getAction().equals(BOOT_COMPLETED)) {
            Logger.logSession("AfterBootTest", "passed");
            config.set(ConfigSMSRec.CONF_TEMP_WHITELISTED_CONTACT, null);
            config.set(ConfigSMSRec.CONF_TEMP_WHITELISTED_CONTACT_ACTIVE_SINCE, null);
            ch.getSettings().set(Settings.SET_GPS_STATE, 1);
            if((Boolean)ch.getSettings().get(Settings.SET_SovaSERVER_UPLOAD_SERVICE)){
                SovaServerService.scheduleJob(context, 0);
                PushReceiver.Register(context);
            }
        }
    }

}
