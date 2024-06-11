package com.ntmsecurity.sova.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ntmsecurity.sova.data.ConfigSMSRec;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;
import com.ntmsecurity.sova.sender.SMS;
import com.ntmsecurity.sova.sender.Sender;
import com.ntmsecurity.sova.utils.Logger;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TempContactExpiredService extends JobService {

    public final static String SENDER_TYPE = "sender";
    public static final String DESTINATION = "dest";

    @Override
    public boolean onStartJob(JobParameters params) {
        Sender sender = null;
        ConfigSMSRec config = JSONFactory.convertJSONConfig(IO.read(JSONMap.class, IO.SMSReceiverTempData));
        String destination = (String) config.get(ConfigSMSRec.CONF_TEMP_WHITELISTED_CONTACT);
        if(destination != null && !destination.isEmpty()) {
            sender = new SMS(destination);
        }

        IO.context = this;
        Logger.init(Thread.currentThread(), this);
        if(sender != null) {
            sender.sendNow("Sova: Pin expired!");
            Logger.logSession("Session expired", sender.getDestination());
        }
        config.set(ConfigSMSRec.CONF_TEMP_WHITELISTED_CONTACT, null);
        config.set(ConfigSMSRec.CONF_TEMP_WHITELISTED_CONTACT_ACTIVE_SINCE, null);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleJob(Context context, Sender sender) {
        ComponentName serviceComponent = new ComponentName(context, TempContactExpiredService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(10 * 1000 * 60);
        builder.setOverrideDeadline(15 * 1000 * 60);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }




}