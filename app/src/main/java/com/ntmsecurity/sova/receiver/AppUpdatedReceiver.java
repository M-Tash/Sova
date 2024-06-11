package com.ntmsecurity.sova.receiver;

import android.content.Context;
import android.content.Intent;


import com.ntmsecurity.sova.data.ConfigSMSRec;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.services.SovaServerService;
import com.ntmsecurity.sova.utils.Logger;

public class AppUpdatedReceiver extends SuperReceiver {

    public static final String APP_UPDATED = "android.intent.action.MY_PACKAGE_REPLACED";

    @Override
    public void onReceive(Context context, Intent intent) {
    init(context);
    if (intent.getAction().equals(APP_UPDATED)){
            Logger.logSession("AppUpdate", "restarted");
            config.set(ConfigSMSRec.CONF_TEMP_WHITELISTED_CONTACT, null);
            config.set(ConfigSMSRec.CONF_TEMP_WHITELISTED_CONTACT_ACTIVE_SINCE, null);
            settings.updateSettings();

            if((Boolean)ch.getSettings().get(Settings.SET_SovaSERVER_UPLOAD_SERVICE)){
                SovaServerService.scheduleJob(context, 0);

            }
        }
    }

}
