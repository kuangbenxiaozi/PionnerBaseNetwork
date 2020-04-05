package com.pioneer.base.network.task;

import android.content.Context;

import com.pioneer.base.network.model.BaseListItemModel;
import com.pioneer.base.network.model.BaseListModel;

import java.util.List;

public abstract class BaseListTask<S,M extends BaseListModel<IM>,IM extends BaseListItemModel> extends BasePostTask<S,M> {

    public BaseListTask(Context context) {
        super(context);
    }

    public List<IM> getDataSet() {
        if(null != model) {
            return model.getDataSet();
        }
        return null;
    }

    public M getModel() {
        return model;
    }

    public int getSize() {
        List<IM> list = getDataSet();
        if(null != list) {
            return list.size();
        }
        return 0;
    }
}
