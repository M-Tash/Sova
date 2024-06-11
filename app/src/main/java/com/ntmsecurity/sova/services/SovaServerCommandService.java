package com.ntmsecurity.sova.services;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;
import com.ntmsecurity.sova.logic.ComponentHandler;
import com.ntmsecurity.sova.net.DataHandler;
import com.ntmsecurity.sova.net.RespListener;
import com.ntmsecurity.sova.sender.FooSender;
import com.ntmsecurity.sova.sender.Sender;
import com.ntmsecurity.sova.utils.Logger;
import com.ntmsecurity.sova.utils.Notifications;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SovaServerCommandService extends JobService implements RespListener {

    private static final int JOB_ID = 109;
    private Settings settings;
    private JobParameters params;

    @SuppressLint("NewApi")
    @Override
    public boolean onStartJob(JobParameters params) {
        IO.context = this;
        settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));
        this.params = params;

        DataHandler dataHandler = new DataHandler(this);
        dataHandler.prepareWithAT(DataHandler.DEFAULT_METHOD, DataHandler.DEFAULT_METHOD, DataHandler.COMMAND, dataHandler.getDefaultATReq(), dataHandler.getEmptyDataReq(), this);
        dataHandler.send();


        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @SuppressLint("NewApi")
    public static void scheduleJobNow(Context context) {
        ComponentName serviceComponent = new ComponentName(context, SovaServerCommandService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent);
        builder.setMinimumLatency(0);
        builder.setOverrideDeadline(1000);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    @Override
    public void onResponseReceived(JSONObject response) {
        try {
            String command = response.getString("Data");
            if (!command.equals("")) {
                Sender sender = new FooSender();
                Logger.init(Thread.currentThread(), this);
                ComponentHandler ch = new ComponentHandler(settings, this, this, params);
                ch.setSender(sender);
                ch.getLocationHandler().setSendToServer(true);
                ch.getMessageHandler().setSilent(true);
                String SovaCommand = (String)settings.get(Settings.SET_Sova_COMMAND);
                if(command.startsWith("423")){
                    Notifications.init(this, false);
                    Notifications.notify(this, "Serveraccess", "Somebody tried three times in a row to log in the server. Access is locked for 10 minutes", Notifications.CHANNEL_SERVER);
                }else {
                    ch.getMessageHandler().handle(SovaCommand + " " + command, this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
