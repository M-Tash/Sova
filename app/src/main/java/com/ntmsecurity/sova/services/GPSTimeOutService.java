package com.ntmsecurity.sova.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ntmsecurity.sova.data.Settings;
import com.ntmsecurity.sova.data.io.IO;
import com.ntmsecurity.sova.data.io.JSONFactory;
import com.ntmsecurity.sova.data.io.json.JSONMap;
import com.ntmsecurity.sova.utils.Logger;
import com.ntmsecurity.sova.utils.SecureSettings;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GPSTimeOutService extends JobService {

    private static final int JOB_ID = 409;

    @Override
    public boolean onStartJob(JobParameters params) {
        IO.context = this;
        Logger.init(Thread.currentThread(), this);
        Settings settings = JSONFactory.convertJSONSettings(IO.read(JSONMap.class, IO.settingsFileName));
        Logger.logSession("GPS", "GPS timed out.");
        if(((Integer)settings.get(Settings.SET_GPS_STATE)) == 2){
            settings.set(Settings.SET_GPS_STATE, 0);
            SecureSettings.turnGPS(this, false);
            Logger.logSession("GPS", "turned off");
        }
        Logger.writeLog();
        SovaServerService.scheduleJob(this, (Integer)settings.get(Settings.SET_SovaSERVER_UPDATE_TIME));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, GPSTimeOutService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent);
        builder.setMinimumLatency(7 * 1000 * 60);
        builder.setOverrideDeadline(10 * 1000 * 60);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public static void cancelJob(Context context){
        JobScheduler jobScheduler = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            jobScheduler = context.getSystemService(JobScheduler.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(jobScheduler.getPendingJob(JOB_ID) != null) {
                jobScheduler.cancel(JOB_ID);
            }
        }
    }
}
