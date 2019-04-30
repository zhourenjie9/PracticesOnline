package net.lzzy.practicesonline.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import net.lzzy.practicesonline.activities.PracticesActivity;
import net.lzzy.practicesonline.models.Practice;
import net.lzzy.practicesonline.utils.AppUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/4/29.
 * Description:
 */
public class DetectWebService extends Service {
    public static final  int FLAG_SERVER_EXCEPTION =0;
    public static final int FLAG_DATA_CHANGED =1;
    public static final int FLAG_DATA_SAME =2;
    public static final int PLAG_SERVER_EXCEPTION =3;
    public static final int NOTIFICATION_DETECT_ID = 0;
    public static final String EXTRA_REFRESH = "extraRefresh";
    private int localCount;
    private NotificationManager manager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        localCount = intent.getIntExtra(PracticesActivity.EXTRA_LOCAL_COUNT,0);
        return new DetectWebBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (manager != null){
            manager.cancel(NOTIFICATION_DETECT_ID);
        }

        return super.onUnbind(intent);
    }

    public class DetectWebBinder extends Binder {




        public void detect() {
            AppUtils.getExecutor().execute( ()-> {

                int flag = compareData();
                if (flag == FLAG_SERVER_EXCEPTION) {
                    notifyUser("服务器无法连接",android.R.drawable.ic_menu_compass,false);

                }else if (flag == FLAG_DATA_CHANGED) {
                    notifyUser("远程服务器有更新",android.R.drawable.ic_popup_sync,true);
                }else {
                    if (manager != null){
                        manager.cancel(NOTIFICATION_DETECT_ID);
                    }
                }
             });
          }

        private void notifyUser(String info, int icon, boolean refresh) {
            Intent intent =new Intent(DetectWebService.this,PracticesActivity.class);
            intent.putExtra(EXTRA_REFRESH,refresh);
            PendingIntent pendingIntent = PendingIntent.getActivity(DetectWebService.this,
                    0,intent,PendingIntent.FLAG_ONE_SHOT);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notification= new  Notification .Builder(DetectWebService.this,"0")
                        .setContentTitle("检测远程服务器")
                        .setContentText(info)
                        .setSmallIcon(icon)
                        .setContentIntent(pendingIntent)
                        .setWhen(System.currentTimeMillis())
                        .build();
            }else {
                notification= new  Notification .Builder(DetectWebService.this)
                        .setContentTitle("检测远程服务器")
                        .setContentText(info)
                        .setSmallIcon(icon)
                        .setContentIntent(pendingIntent)
                        .setWhen(System.currentTimeMillis())
                        .build();
            }
            if (manager!=null){
                manager.notify(NOTIFICATION_DETECT_ID,notification);
            }
        }

        private int compareData() {
            try {
                List<Practice> remote =
                        PracticeService.getPractices(PracticeService.getPracticesFromServer());
                if (remote.size() != localCount) {
                    return FLAG_DATA_CHANGED;
                }else {
                    return FLAG_DATA_SAME;
                }
            } catch (Exception e) {
               return PLAG_SERVER_EXCEPTION;
            }

        }
    }
}
