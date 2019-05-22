package net.lzzy.practicesonline.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import net.lzzy.practicesonline.utils.AppUtils;

/**
 * Created by lzzy_gxy on 2019/4/11.
 * Description:
 */
public abstract class BaseActivity  extends AppCompatActivity {

    private Fragment fragment;
    private FragmentManager manager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutRes());
        splashFragment();
            AppUtils.addActivity(this);
            FragmentManager manager =getSupportFragmentManager();
            fragment =manager.findFragmentById(getContainerId());
            if (fragment== null){
                fragment =createFragment();
                manager.beginTransaction().add(getContainerId(),fragment).commit();
            }
    }
    protected Fragment getFragment(){
        return fragment;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    public void splashFragment() {
        AppUtils.addActivity(this);
        manager =
                getSupportFragmentManager();
        fragment = manager.findFragmentById(getContainerId());
        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction().add(getContainerId(), fragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.setRunning(getLocalClassName()) ;
    }

    protected FragmentManager getManager(){
        return manager;
    }
    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.setStopped(getLocalClassName());
    }
    /**
     * Activity的布局文件id
     * @return布局资源id
     */
    protected  abstract int getLayoutRes();

    /**
     * fragment容器的id
     * @return id
     */

    protected abstract int getContainerId();

    /**
     * 生成托管的Fragment对象
     * @return fragment
     */

    protected abstract  Fragment createFragment();
}
