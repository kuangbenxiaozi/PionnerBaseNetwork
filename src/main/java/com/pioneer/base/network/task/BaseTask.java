package com.pioneer.base.network.task;


import com.pioneer.base.network.listener.CommonSubscriber;
import com.pioneer.base.network.model.BaseModel;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public abstract class BaseTask<M extends BaseModel> {
    public abstract Disposable requestData(CommonSubscriber<M> subscriber);

    public abstract Observable<M> getRequestObservable();
}

