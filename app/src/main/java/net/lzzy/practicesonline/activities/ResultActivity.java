package net.lzzy.practicesonline.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;

import net.lzzy.practicesonline.fragments.ChartFragment;
import net.lzzy.practicesonline.fragments.GridFragment;
import net.lzzy.practicesonline.models.view.QuestionResult;

import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/13.
 * Description:
 */
public class ResultActivity extends BaseActivity implements ChartFragment.OnResultSwitchListener,
         GridFragment.OnResultListener {

    public static final String RESULT_POSITION = "resultPosition";
    public static final String QUESTION = "question";
    private List<QuestionResult> results;
    private String practiceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        practiceId = getIntent().getStringExtra(QuestionActivity.EXTRA_PRACTICE_ID);

//        FragmentManager manager=getSupportFragmentManager();
//        Fragment fragment=manager.findFragmentById(R.id.activity_result_container);
//        if (fragment==null){
//            fragment= GridFragment.newInstance(practiceId, results);
//            manager.beginTransaction().add(R.id.activity_result_container,fragment).commit();
//        }

    }



    @Override
    protected int getLayoutRes() {
        return R.layout.activity_result;
    }



    @Override
    protected int getContainerId() {
        return R.id.activity_result_container;
    }

    @Override
    protected Fragment createFragment() {
        results = getIntent().getParcelableArrayListExtra(QuestionActivity.EXTRA_RESULT);
        return GridFragment.newInstance(results);
    }

    @Override
    public void gotoGrid() {
        getManager().beginTransaction().replace(getContainerId()
                , GridFragment.newInstance(results)).commit();
    }

    @Override
    public void onResultTopic(int pos) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_POSITION, pos);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClickImgButton() {
        getManager().beginTransaction().replace(getContainerId()
                , ChartFragment.newInstance(results)).commit();
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("返回")
                .setPositiveButton("查看收藏", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.putExtra(QUESTION, true);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .setNegativeButton("章节列表", (dialog, which) ->
                        startActivity(new Intent(this, PracticesActivity.class))
                )
                .setNeutralButton("返回题目", (dialog, which) ->
                        finish()
                )
                .show();
    }
}
