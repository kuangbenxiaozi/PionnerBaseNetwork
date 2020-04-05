package com.pioneer.base.network.task;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.pioneer.base.network.model.BaseModel;

import java.util.Map;

/**
 * get请求方式的基础任务
 * @param <S> service接口
 * @param <M> 返回数据的类型
 */
public abstract class BaseGetTask<S,M extends BaseModel> extends BaseRetrofitTask<S,M> {
    protected Map<String,String> mQueryParams = new ArrayMap<>();

    public BaseGetTask(Context context) {
        super(context);
    }

    public void addQueryParams(String key,String value) {
        if(null == mQueryParams) {
            mQueryParams = new ArrayMap<>();
        }

        if(!TextUtils.isEmpty(key)) {
            mQueryParams.put(key,TextUtils.isEmpty(value) ? "" : value);
        }
    }
}
