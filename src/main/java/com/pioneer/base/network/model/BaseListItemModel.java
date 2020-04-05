package com.pioneer.base.network.model;

import java.io.Serializable;

public abstract class BaseListItemModel implements Comparable<BaseListItemModel>, Serializable {
    private static final long serialVersionUID = 5865237684840547901L;

    private int mPosition;

    private transient boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int compareTo(BaseListItemModel other) {
        return 1;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public String getId() {
        return null;
    }
}
