package net.lzzy.practicesonline.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.fragments.PracticesFragment;
import net.lzzy.practicesonline.models.PracticeFactory;
import net.lzzy.practicesonline.network.DetectWebService;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.utils.ViewUtils;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public class PracticesActivity extends BaseActivity implements PracticesFragment.PracticeSelectedListener{

    public static final String EXTRA_PRACTICE_ID ="extraPracticeId";
    public static final String EXTRA_API_ID ="extraApiId";
    public static  final String EXTRA_LOCAL_COUNT="extraLocalCont";
    private ServiceConnection connection;
    private  boolean refresh = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        if (getIntent() != null){
            refresh = getIntent().getBooleanExtra(DetectWebService.EXTRA_REFRESH,false);
        }
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DetectWebService.DetectWebBinder binder = (DetectWebService.DetectWebBinder) service;
                binder.detect();

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        int localCount = PracticeFactory.getInstance().get().size();
        Intent intent = new Intent(this,DetectWebService.class);
        intent.putExtra(EXTRA_LOCAL_COUNT,localCount);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (refresh){
            ( (PracticesFragment)getFragment()).startRefresh();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("退出应用吗？")
                .setPositiveButton("退出",(dialog, which) -> AppUtils.exit())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private void initViews() {
        SearchView search=findViewById(R.id.bar_title_search);
        search.setQueryHint("请输入关键词搜索");
        search.setOnQueryTextListener(new ViewUtils.AbstractQueryListener() {
            @Override
            public void handleQuery(String kw) {
                ( (PracticesFragment) getFragment()).search(kw);
            }
        });
        SearchView.SearchAutoComplete auto =search.findViewById(R.id.search_src_text);
        auto.setHintTextColor(Color.WHITE);
        auto.setTextColor(Color.WHITE);
        ImageView icon = search.findViewById(R.id.search_button);
        ImageView icX =search.findViewById(R.id.search_close_btn);
        ImageView icG = search.findViewById(R.id.search_go_btn);
        icon.setColorFilter(Color.WHITE);
        icG.setColorFilter(Color.WHITE);
        icX.setColorFilter(Color.WHITE);
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_practices;
    }

    @Override
    protected int getContainerId() {
        return R.id.activity_practic_container;
    }

    @Override
    protected Fragment createFragment() {
        return new  PracticesFragment();
    }

    @Override
    public void onPracticeSelected(String practiceId, int apiId) {
        Intent intent =new Intent(this,QuestionActivity.class);
        intent.putExtra(EXTRA_PRACTICE_ID,practiceId);
        intent.putExtra(EXTRA_API_ID,apiId);
        startActivity(intent);

    }
}
