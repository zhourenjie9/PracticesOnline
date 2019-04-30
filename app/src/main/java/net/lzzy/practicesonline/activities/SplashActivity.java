package net.lzzy.practicesonline.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.constants.ApiConstants;
import net.lzzy.practicesonline.fragments.SplashFragment;
import net.lzzy.practicesonline.utils.AbstractStatiCHandler;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.utils.ViewUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

public class SplashActivity extends BaseActivity implements  SplashFragment.OnSplashFinishedListener {
private SplashHandler handler =new SplashHandler(this);
    private static final int WHAT_COUNTING=0;
    private static final int WHAT_COUNT_DONE=1;
    private static final int WHAT_EXCEPTION=2;
    private static final int WHAT_SERVER_OFF=3;
    private int seconds =10;

    private TextView tvCount;
    private boolean isServerOn = true;

    private static class SplashHandler extends AbstractStatiCHandler<SplashActivity>{


        SplashHandler(SplashActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, SplashActivity activity) {
            switch (msg.what){
                case WHAT_COUNTING:
                    String display = msg.obj +"秒";
                    activity.tvCount.setText(display);
                    break;
                case WHAT_COUNT_DONE:
                    if (activity.isServerOn){
                        activity.gotMain();
                    }

                    break;
                case WHAT_EXCEPTION:
                    new AlertDialog.Builder(activity)
                            .setMessage(msg.obj.toString())
                            .setPositiveButton("继续",(dialog, which) -> activity.gotMain())
                            .setNegativeButton("退出",(dialog, which) -> AppUtils.exit())
                            .show();

                    break;
                case WHAT_SERVER_OFF:
                    Activity context = AppUtils .getRunningActivity();
                    new AlertDialog.Builder(Objects.requireNonNull(context))
                            .setMessage("服务器没有响应，是否继续？\n"+msg.obj)
                            .setPositiveButton("确定",((dialog, which) -> {
                                if (context instanceof SplashActivity){
                                    ((SplashActivity)context).gotMain();
                                }
                            }
                                    ))
                            .setNegativeButton("退出",(dialog, which) -> AppUtils.exit())
                            .setNeutralButton("设置",((dialog, which) -> {}))
                            .show();
                    break;
                    default:
                        break;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvCount = findViewById(R.id.activity_splash_count_down);
        tvCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seconds= 0;
            }
        });
        if (!AppUtils.ieNetworkAvailable()) {
            new AlertDialog.Builder(this)
                    .setMessage("网咯不可用,是否继续")
                    .setPositiveButton("退出", (dialog, which) -> AppUtils.exit())
                    .setPositiveButton("确定", (dialog, which) -> ViewUtils.gotoSetting(AppUtils.getContext()))
                    .show();
        } else {
            ThreadPoolExecutor executor = AppUtils.getExecutor();
            executor.execute(this::countDown);
            executor.execute(this::detectServerStatus);
        }
    }


    private void countDown(){
        while (seconds >= 0){
            handler.sendMessage(handler.obtainMessage(WHAT_COUNTING,seconds));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
             handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION,e.getMessage()));
            }
            seconds--;
        }
        handler.sendEmptyMessage(WHAT_COUNT_DONE);

    }

    private void detectServerStatus(){
        try {
            AppUtils.tryConnectServer(ApiConstants.URL_API);
        } catch (IOException e) {
            isServerOn = false;
           handler.sendMessage(handler.obtainMessage(WHAT_SERVER_OFF,e.getMessage()));
        }
    }


    public void gotMain () {
        startActivity(new Intent(this,PracticesActivity.class));
        finish();
        }


    @Override
    public void cancelCount() {
        seconds= 0;
    }


    @Override
    protected int getLayoutRes() {
        return  R.layout.activity_splash;
    }

    @Override
    protected int getContainerId() {
        return R.id.fragment_splash_container;
    }

    @Override
    protected Fragment createFragment() {
        return new SplashFragment();
    }
}
