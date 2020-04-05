package com.pioneer.base.network.task;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.pioneer.base.network.model.BaseModel;

import java.util.Map;

public abstract class BasePostTask<S,M extends BaseModel> extends BaseRetrofitTask<S,M> {
    protected Map<String,String> mFormParams = new ArrayMap<String,String>();
    protected Map<String,String> mQueryParams = new ArrayMap<String,String>();

    public BasePostTask(Context context) {
        super(context);
    }

    public void addFormParams(String name,String value) {
        if(null == mFormParams) {
            mFormParams = new ArrayMap<>();
        }

        if(!TextUtils.isEmpty(name)) {
            mFormParams.put(name,TextUtils.isEmpty(value) ? "" : value);
        }
    }

    public void addQueryParams(String name,String value) {
        if(null == mQueryParams) {
            mQueryParams = new ArrayMap<>();
        }

        if(!TextUtils.isEmpty(name)) {
            mQueryParams.put(name,TextUtils.isEmpty(value) ? "" : value);
        }
    }
}
