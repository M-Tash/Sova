package com.ntmsecurity.sova.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;

import java.util.Calendar;

import com.ntmsecurity.sova.data.ConfigSMSRec;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.WhiteList;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;
import com.ntmsecurity.sova.data.io.json.JSONWhiteList;
import com.ntmsecurity.sova.logic.ComponentHandler;
import com.ntmsecurity.sova.utils.Logger;
import com.ntmsecurity.sova.utils.Notifications;
import com.ntmsecurity.sova.utils.Permission;

abstract class SuperReceiver extends BroadcastReceiver {

    protected WhiteList whiteList;
    protected ConfigSMSRec config;
    protected Settings settings;

    protected ComponentHandler ch;

    protected void init(Context context) {
        IO.context = context;
        Logger.init(Thread.currentThread(), context);
        whiteList = JSONFactory.convertJSONWhiteList(IO.read(JSONWhiteList.class, IO.whiteListFileName));
        settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));
        config = JSONFactory.convertJSONConfig(IO.read(JSONMap.class, IO.SMSReceiverTempData));
        if (config.get(ConfigSMSRec.CONF_LAST_USAGE) == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -5);
            config.set(ConfigSMSRec.CONF_LAST_USAGE, cal.getTimeInMillis());
        }
        Notifications.init(context, false);
        Permission.initValues(context);
        ch = new ComponentHandler(settings, context, null, null);
    }

}
