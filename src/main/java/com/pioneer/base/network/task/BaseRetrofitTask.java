package com.pioneer.base.network.task;

import android.content.Context;
import android.util.ArrayMap;

import com.pioneer.base.network.NetConstant;
import com.pioneer.base.network.listener.CommonSubscriber;
import com.pioneer.base.network.model.BaseModel;
import com.pioneer.base.network.retrofitmanager.BaseRetrofitManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public abstract class BaseRetrofitTask<S,M extends BaseModel> extends BaseTask<M> {
    private Context mContext;
    private Disposable mDisposable;
    protected Map<String,String> mHeaderParams = new ArrayMap<>();
    protected M model;

    protected BaseRetrofitTask(Context context) {
        mContext = context;
        if(null == mHeaderParams) {
            mHeaderParams = new ArrayMap<>();
        }
    }

    /**
     * host必须以"/"结束
     * @return
     */
    protected String getHost() {
        return NetConstant.BASE_HOST;
    }

    /**
     * Path 前后都不能有"/"
     * @return
     */
    protected String getPath() {
        return "";
    }

    private S createObservable() {
        Type t = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) t).getActualTypeArguments();
        return BaseRetrofitManager.getInstance().createRetrofit(mContext,getHost()).create((Class<S>) params[0]);
    }

    protected abstract Observable<M> createActualObservable(S s);

    @Override
    public Disposable requestData(final CommonSubscriber<M> subscriber) {
        mDisposable = getRequestObservable().subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<M>() {
            @Override
            public void accept(M m) throws Exception {
                model = m;
                subscriber.onNext(m);
            }
        },new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                model = null;
                subscriber.onError(throwable);
            }
        },new Action() {
            @Override
            public void run() throws Exception {
                subscriber.onComplete();
            }
        },new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                subscriber.onSubscribe(disposable);
            }
        });
        return mDisposable;
    }

    @Override
    public Observable<M> getRequestObservable() {
        return createActualObservable(createObservable());
    }

    public boolean isDisposed() {
        return null != mDisposable && mDisposable.isDisposed();
    }

    public void dispose() {
        if(null != mDisposable) {
            mDisposable.dispose();
        }
    }

    public void setDisposed(Disposable disposed) {
        this.mDisposable = disposed;
    }
}
