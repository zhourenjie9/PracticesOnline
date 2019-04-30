package net.lzzy.practicesonline.fragments;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.models.Practice;
import net.lzzy.practicesonline.models.PracticeFactory;
import net.lzzy.practicesonline.models.Question;
import net.lzzy.practicesonline.models.UserCookies;
import net.lzzy.practicesonline.network.DetectWebService;
import net.lzzy.practicesonline.network.PracticeService;
import net.lzzy.practicesonline.network.QuestionService;
import net.lzzy.practicesonline.utils.AbstractStatiCHandler;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.utils.DateTimeUtils;
import net.lzzy.practicesonline.utils.ViewUtils;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public class PracticesFragment extends BaseFragment {

    private ListView lv;
    private SwipeRefreshLayout swipe;
    private TextView tvHint;
    private TextView tvTime;
    private List<Practice> practices;
    private GenericAdapter<Practice> adapter;
    private PracticeFactory factory= PracticeFactory.getInstance();
    private ThreadPoolExecutor executor = AppUtils.getExecutor();
    private DownloadHandler handler =new DownloadHandler(this);
    private static final int WHAT_PRACTICE_DONE=0;
    private static final int WHAT_EXCEPTION=1;
    private static final int WHAT_QUESTION_DONE =2;
    private static final int WHAT_QUESTION_EXCEPTION =3;

    private PracticeSelectedListener listener;


    private static class  DownloadHandler extends AbstractStatiCHandler<PracticesFragment>{

        public DownloadHandler(PracticesFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, PracticesFragment fragment) {
            switch (msg.what) {
                case WHAT_PRACTICE_DONE:
                    try {
                        fragment.tvTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(new Date()));
                        UserCookies.getInstance().updateLastRefreshTime();
                        List<Practice> practices = PracticeService.getPractices(msg.obj.toString());
                        for (Practice practice : practices) {
                            fragment.adapter.add(practice);
                        }
                        Toast.makeText(fragment.getContext(), "同步完成", Toast.LENGTH_SHORT).show();
                        fragment.finishRefresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                        fragment.handlePracticeException(e.getMessage());
                    }
                    break;
                case WHAT_EXCEPTION:
                    fragment.handlePracticeException(msg.obj.toString());
                    break;
                case WHAT_QUESTION_DONE:
                    UUID practiceId = fragment.factory.getPracticeId(msg.arg1);
                    fragment.saveQuestions(msg.obj.toString(), practiceId);
                    ViewUtils.dismissProgress();
                    break;
                case WHAT_QUESTION_EXCEPTION:
                    ViewUtils.dismissProgress();
                    Toast.makeText(fragment.getContext(), "下载失败请重试\n"+msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();

                default:
                    break;
            }

        }
    }

    private void saveQuestions(String json,  UUID practiceId) {
        try {
            List<Question> questions = QuestionService.getQuestions(json,practiceId);
            factory.saveQuestions(questions,practiceId);
            for (Practice practice : practices){
                if (practice.getId().equals(practiceId)){
                    practice.setDownloaded(true);
                }
            }
           adapter.notifyDataSetChanged();
        } catch (Exception e) {
         Toast.makeText(getContext(),"下载失败请重试!\n"+e.getMessage(),
                 Toast.LENGTH_SHORT).show();
        }
    }

    static class practiceDownloader extends AsyncTask<Void,Void,String>{
        WeakReference<PracticesFragment> fragment;

        practiceDownloader( PracticesFragment fragment){
            this.fragment =new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PracticesFragment fragment = this.fragment.get();
            fragment.tvTime.setVisibility(View.VISIBLE);
            fragment.tvHint.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return PracticeService.getPracticesFromServer();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PracticesFragment fragment =this.fragment.get();
            fragment.tvTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(new Date()));
            UserCookies.getInstance().updateLastRefreshTime();
            try {
                List<Practice> practices = PracticeService.getPractices(s);
                for (Practice practice : practices) {
                    fragment.  adapter.add(practice);
                }
                Toast.makeText(fragment.getContext(), "同步完成", Toast.LENGTH_SHORT).show();
                fragment. finishRefresh();
            } catch (Exception e) {
                e.printStackTrace();
                fragment. handlePracticeException(e.getMessage());
            }
        }
    }

    static class QuestionDownloader extends  AsyncTask<Void,Void,String>{
        WeakReference<PracticesFragment> fragment;
        Practice practice;

        QuestionDownloader(PracticesFragment fragment,Practice practice){
            this.fragment =new WeakReference<>(fragment);
            this.practice = practice;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ViewUtils.showProgress(fragment.get().getContext(),"开始下载题目……");
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return QuestionService.getQuestionsOfPracticeFromServer(practice.getApiId());
            } catch (IOException e) {
              return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           fragment.get().saveQuestions(s,practice.getId());
           ViewUtils.dismissProgress();

        }
    }

    private void handlePracticeException(String message) {
        finishRefresh();
//        Snackbar.make(lv,"同步失败\n"+message.Snackbar.LENGTH_LONG)
//                .setAction("重试",v-> {
//                    swipe.setRefreshing(true);
//                    refreshListener.onRefresh();
//                }).show();

    }

    private void finishRefresh() {
        swipe.setRefreshing(false);
        tvTime.setVisibility(View.GONE);
        tvHint.setVisibility(View.GONE);
        NotificationManager manager = (NotificationManager) Objects.requireNonNull(getContext())
                .getSystemService(Context.NOTIFICATION_SERVICE);
       if (manager !=null){
            manager.cancel(DetectWebService.NOTIFICATION_DETECT_ID);
        }
    }

    public void startRefresh(){
        swipe.setRefreshing(true);
        refreshListener.onRefresh();
    }

    @Override
    protected void populate() {
     initViews();
     loadPractices();
     initSwipe();

    }
    private SwipeRefreshLayout.OnRefreshListener refreshListener = this::downloadpracticesAsync;

    private void downloadpractices() {
        tvTime.setVisibility(View.VISIBLE);
        tvHint.setVisibility(View.VISIBLE);
        executor.execute(()->{
            try {
                String json = PracticeService.getPracticesFromServer();
                handler.sendMessage(handler.obtainMessage(WHAT_PRACTICE_DONE,json));
            } catch (IOException e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION,e.getMessage()));
            }
//            int seconds =5;
//
//            while (seconds >=0){
//                try {
//                    Thread.sleep(1000);
//                    seconds--;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            handler.sendEmptyMessage(0);
        });

    }

    private void downloadpracticesAsync(){
        new practiceDownloader(this).execute();
    }

    private void initSwipe() {
        swipe.setOnRefreshListener(refreshListener);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean isTop = view.getChildCount() ==0 || view.getChildAt(0).getTop() >0;
                swipe.setEnabled(isTop);
            }
        });
    }

    private void loadPractices(){
        practices = factory.get();
        Collections.sort(practices, (o1, o2) -> o2.getDownloadDate().compareTo(o1.getDownloadDate()));
        adapter = new GenericAdapter<Practice>(getContext(),R.layout.practice_item,practices) {
            @Override
            public void populate(ViewHolder holder, Practice practice) {
                holder.setTextView(R.id.practice_item_tv_name,practice.getName());
                Button btnOutlines =holder.getView(R.id.practice_item_btn_outlines);
                if (practice.isDownloaded()){
                    btnOutlines.setVisibility(View.VISIBLE);

                            btnOutlines.setOnClickListener(v ->  new AlertDialog.Builder(getActivity())
                                    .setMessage(practice.getOutlines())
                                    .show());
                }else {
                    btnOutlines.setVisibility(View.GONE);
                }
                Button btnDel = holder.getView(R.id.practice_item_btn_del);
                btnDel.setOnClickListener(v ->  new AlertDialog.Builder(getContext())
                        .setMessage("要删除该章节及题目吗？")
                        .setPositiveButton("删除",(dialog,which)->{
                    isDeleting=false;
                    adapter.remove(practice);
                })
                        .setNegativeButton("取消",null)
                        .show());
                int visible =isDeleting ? View.VISIBLE : View.GONE;
                btnDel.setVisibility(visible);
                holder.getConvertView().setOnTouchListener(new ViewUtils.AbstractTouchListener() {
                    @Override
                    public boolean handleTouch(MotionEvent event) {
                        slideToDelte(event,btnDel,practice);
                        return true;
                    }
                });


            }

            @Override
            public boolean persistInsert(Practice practice) {
                return factory.add(practice);
            }

            @Override
            public boolean persistDelete(Practice practice) {
                return factory.deletePracticeAndRelated(practice);
            }
        };
        lv.setAdapter(adapter);
    }
    private float touchX1;
    private static final float MIN_DISTANCE=100;
    private boolean isDeleting=false;
    private void slideToDelte(MotionEvent event, Button btnDel, Practice practice){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchX1=event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float touchX2=event.getX();
                if (touchX1-touchX2>MIN_DISTANCE){
                    if (!isDeleting){
                        btnDel.setVisibility(View.VISIBLE);
                        isDeleting=true;
                    }
                }else {
                    if (btnDel.isShown()){
                        btnDel.setVisibility(View.GONE);
                        isDeleting=false;
                    }else if (!isDeleting){
                        performItemClick(practice);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void performItemClick(Practice practice) {
        if (practice.isDownloaded() && listener != null){
          listener.onPracticeSelected(practice.getId().toString(),practice.getApiId());
        }else {
            new AlertDialog.Builder(getContext())
                    .setMessage("下载该章节题目吗？")
                    .setPositiveButton("下载",(dialog,which) -> downloadQuestionsAsync(practice))
                    .setNegativeButton("取消",null)
                    .show();
        }
    }
    private void downloadQuetions(int apiid){
       ViewUtils.showProgress(getContext(),"开始下载题目……");
       executor.execute(() -> {
           try {
               String json = QuestionService.getQuestionsOfPracticeFromServer(apiid);
               Message msg = handler.obtainMessage(WHAT_PRACTICE_DONE,json);
               msg.arg1 =apiid;
           } catch (IOException e) {
             handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION,e.getMessage()));
           }
       });
    }

    private void downloadQuestionsAsync(Practice practice){
        new QuestionDownloader(this,practice).execute();
    }

    private void initViews() {
          lv= find(R.id.fragment_practices_lv);
        TextView tvNone = find(R.id.fragment_practices_tv_none);
        lv.setEmptyView(tvNone);
         swipe = find(R.id.fragment_practices_swipe);
         tvHint = find(R.id.fragment_practices_tv_hint);
         tvTime= find(R.id.fragment_practices_tv_time);
         tvTime.setText(UserCookies.getInstance().getLastRefreshTime());
         tvHint.setVisibility(View.GONE);
         tvTime.setVisibility(View.GONE);
         find(R.id.fragment_practices_lv).setOnTouchListener(new ViewUtils.AbstractTouchListener() {
             @Override
             public boolean handleTouch(MotionEvent event) {
                isDeleting =false;
                adapter.notifyDataSetChanged();
                return false;
             }
         });

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_practices;
    }

    @Override
    public void search(String kw) {
        practices.clear();
        if (kw.isEmpty()){
            practices.addAll(factory.get());
        }else {
            practices.addAll(factory.search(kw));
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof  PracticeSelectedListener){
            listener = (PracticeSelectedListener) context;
        }else {
            throw new ClassCastException(context+"必须实现PracticeSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener =null;
        handler.removeCallbacksAndMessages(null);
    }
    public interface PracticeSelectedListener{
        void onPracticeSelected(String practiceId,int apiId);
    }
}
