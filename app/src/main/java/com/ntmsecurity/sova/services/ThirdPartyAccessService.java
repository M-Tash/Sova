package com.ntmsecurity.sova.services;

import android.content.Context;
import android.os.Build;
import android.provider.Telephony;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.Calendar;

import com.ntmsecurity.sova.data.ConfigSMSRec;
import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.WhiteList;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;
import com.ntmsecurity.sova.data.io.json.JSONWhiteList;
import com.ntmsecurity.sova.logic.ComponentHandler;
import com.ntmsecurity.sova.sender.NotificationReply;
import com.ntmsecurity.sova.utils.Logger;
import com.ntmsecurity.sova.utils.Notifications;
import com.ntmsecurity.sova.utils.Permission;

public class ThirdPartyAccessService extends NotificationListenerService {

    protected WhiteList whiteList;
    protected ConfigSMSRec config;

    protected ComponentHandler ch;
    protected  String DEFAULT_SMS_PACKAGE_NAME = "";

    protected void init(Context context) {
        IO.context = context;
        Logger.init(Thread.currentThread(), context);
        whiteList = JSONFactory.convertJSONWhiteList(IO.read(JSONWhiteList.class, IO.whiteListFileName));
        Settings settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));
        config = JSONFactory.convertJSONConfig(IO.read(JSONMap.class, IO.SMSReceiverTempData));
        if (config.get(ConfigSMSRec.CONF_LAST_USAGE) == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -5);
            config.set(ConfigSMSRec.CONF_LAST_USAGE, cal.getTimeInMillis());
        }
        Notifications.init(context, false);
        Permission.initValues(context);
        ch = new ComponentHandler(settings, context, null, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            DEFAULT_SMS_PACKAGE_NAME = Telephony.Sms.getDefaultSmsPackage(context);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        init(this);
        CharSequence msgCS = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            msgCS = sbn.getNotification().extras.getCharSequence("android.text");
        }
        if(sbn.getPackageName().equals(DEFAULT_SMS_PACKAGE_NAME)){
            return;
        }
        if(msgCS != null) {
            NotificationReply sender = new NotificationReply(this, sbn);
            if(sender.canSend()) {
                ch.setSender(sender);
                String msg = msgCS.toString();
                String msgLower = msg.toLowerCase();
                String Sovacommand = (String) ch.getSettings().get(Settings.SET_Sova_COMMAND);
                if (msgLower.contains(Sovacommand)) {
                    msg = ch.getMessageHandler().checkAndRemovePin(msg);
                    if (msg != null) {
                        ch.getMessageHandler().handle(msg, this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            cancelNotification(sbn.getKey());
                        }
                    }
                }
            }
            if((Boolean)ch.getSettings().get(Settings.SET_Sova_LOW_BAT_SEND)) {
                if (sbn.getPackageName().equals("com.android.systemui")) {
                    if (sbn.getTag().equals("low_battery")) {
                        ch.setSender(sender);
                        String Sovacommand = (String) ch.getSettings().get(Settings.SET_Sova_COMMAND);
                        ch.getMessageHandler().handle(Sovacommand + " locate", this);
                    }
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }



}
