package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
    protected View mConvertView;
    protected ClickListener mClickListener;
    protected TouchListener mTouchListener;
    protected int mLayoutTag;

    public AbstractViewHolder() {
        super(null);
    }

    public AbstractViewHolder(View itemView) {
        super(itemView);
        this.initView(itemView);
    }

    protected abstract void initView(View itemView);

    public abstract void setupView(Context ctx, int groupPosition, int itemPosition, BaseItem itemobject);

    public void setOnClickListener(ClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public void setOnTouchListener(TouchListener touchListener) {
        this.mTouchListener = touchListener;
    }

    public void setLayoutTag(int layoutTag) {
        this.mConvertView.setTag(layoutTag);
        this.mLayoutTag = layoutTag;
    }

    public interface ClickListener extends View.OnClickListener {
        @Override
        public void onClick(View view);
    }

    public interface TouchListener extends View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent mv);
    }
}
