package com.pioneer.base.network.listener;

import com.pioneer.base.network.model.BaseModel;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CommonSubscriber<M extends BaseModel> implements Observer<M> {

    private OnSubscriberListener<M> onSubscriberListener;


    public CommonSubscriber() {
    }

    public CommonSubscriber(OnSubscriberListener<M> onSubscriberListener) {
        this.onSubscriberListener = onSubscriberListener;
    }

    public void setOnSubscriberListener(OnSubscriberListener<M> onSubscriberListener) {
        this.onSubscriberListener = onSubscriberListener;
    }

    /**
     * 订阅开始时调用
     */
    @Override
    public void onSubscribe(Disposable d) {
        if (null != onSubscriberListener) {
            onSubscriberListener.onStart(d);
        }
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param m 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(M m) {
        if (onSubscriberListener != null) {
            onSubscriberListener.onSuccess(m);
        }
    }

    /**
     * 对错误进行统一处理
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (null != onSubscriberListener) {
            onSubscriberListener.onFailure(getExceptionType(e), e);
        }
    }

    /**
     * 完成
     */
    @Override
    public void onComplete() {
        if (null != onSubscriberListener) {
            onSubscriberListener.onComplete();
        }
    }

    private EXCEPTION_TYPE getExceptionType(Throwable e) {
        EXCEPTION_TYPE type = EXCEPTION_TYPE.THROWABLE;
        if (null != e) {
            if (e instanceof SocketTimeoutException) {
                type = EXCEPTION_TYPE.EXCEPTION_SOCKET_TIMEOUT;
            } else if (e instanceof UnknownHostException) {
//                if (NetworkStatsUtil.checkNetStatus(mContext) == NetworkStatsUtil.DISCONNECTED_NETWORK) {
//                    type = EXCEPTION_TYPE.EXCEPTION_DISCONNECTED_NET;
//                } else {
//                    type = EXCEPTION_TYPE.EXCEPTION_UNKNOWN_HOST;
//                }
                type = EXCEPTION_TYPE.EXCEPTION_UNKNOWN_HOST;
            } else if (e.getMessage() != null && e.getMessage().contains("Unexpected code from onResponse")) {
                type = EXCEPTION_TYPE.EXCEPTION_IO_UNEXPECTED_CODE;
            } else {
                type = EXCEPTION_TYPE.EXCEPTION_IO;
            }
        }
        return type;
    }

    /**
     * 网络请求成功，Obj为HttpResponse
     */
    public final static int SUCCESS = 1;

    /**
     * 网络请求失败，Obj为Exception异常
     */
    public final static int EXCEPTION = 2;

    public enum EXCEPTION_TYPE {
        SERVER_EXCEPTION(0),
        IO_EXCEPTION(1),
        PARSE_EXCEPTION(2),
        COOKIE_EXCEPTION(3),
        SOCKETTIMEOUT_EXCEPTION(4),
        UNKNOWN_HOST_EXCEPTION(5),
        JSON_IO_EXCEPTION(6),
        EXCEPTION_CODE_APPLAYER(7),
        EXCEPTION_IO(8),
        EXCEPTION_PROCESS_RESPONSE(9),
        EXCEPTION_COOKIE(10),
        EXCEPTION_SOCKET_TIMEOUT(11),
        EXCEPTION_UNKNOWN_HOST(12),
        EXCEPTION_JSON_IO(13),
        EXCEPITON_FILE_NOT_FOUND(14),
        NO_EXCEPTION(15),
        EXCEPTION_NET(16),
        EXCEPTION_UNKNOW(17),
        EXCEPTION_IO_UNEXPECTED_CODE(18),
        EXCEPTION_DISCONNECTED_NET(19),
        EXCEPTION_JSON_SYNTAX(20),
        EXCEPTION_FRESCO_REQUEST(21),

        THROWABLE(22);
        private int index;

        EXCEPTION_TYPE(int i) {
            this.index = i;
        }

        public int getIndex() {
            return index;
        }
    }
}
