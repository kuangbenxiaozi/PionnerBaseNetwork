package com.pioneer.base.network.listener;

import com.pioneer.base.network.model.BaseModel;

import io.reactivex.disposables.Disposable;

public interface OnSubscriberListener<M extends BaseModel> {
    void onStart(Disposable disposable);

    void onComplete();

    void onSuccess(M model);

    void onFailure(CommonSubscriber.EXCEPTION_TYPE type,Throwable t);
}
