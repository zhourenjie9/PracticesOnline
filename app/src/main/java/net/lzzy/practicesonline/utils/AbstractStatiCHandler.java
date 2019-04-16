package net.lzzy.practicesonline.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by lzzy_gxy on 2019/4/12.
 * Description:
 */
public abstract class AbstractStatiCHandler<T> extends Handler {
    private final WeakReference<T> context;

    public AbstractStatiCHandler(T context){
        this.context =new WeakReference<>(context);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T t =context.get();
        handleMessage(msg,t);
    }

    /**
     * 处理消息的业务逻辑
     * @param msg Message对象
     * @param t Activity or Fragment对象
     */
    public abstract void handleMessage(Message msg, T t);
}
