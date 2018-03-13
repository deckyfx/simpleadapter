package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class AbstractViewHolder<T extends BaseItem> extends RecyclerView.ViewHolder {
    protected View mConvertView;
    protected int mLayoutTag;

    public AbstractViewHolder(View itemView) {
        super(itemView);
        this.mConvertView = itemView;
    }

    public abstract void setupView(Context ctx, int groupPosition, int itemPosition, T item);

    public void setLayoutTag(int layoutTag) {
        this.mConvertView.setTag(layoutTag);
        this.mLayoutTag = layoutTag;
    }

    public View getView() {
        return this.mConvertView;
    }
}
