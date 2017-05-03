package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Filter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by decky on 8/3/16.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<AdapterItem.RecycleViewHolder> {
    private AdapterDataSet<AdapterItem> mItemsList, mOriginalList;
    private int mItemLayout;
    private Context mCtx;
    private Class<? extends AdapterItem.RecycleViewHolder> mViewHolderClass;
    private ClickListener mClickListener;
    private TouchListener mTouchListener;
    private Filter mFilter;
    private AnimationSet mScrollAnimation;
    private int mCountMargin;

    public RecycleViewAdapter(Context ctx, AdapterDataSet<AdapterItem> itemsList) {
        this(ctx, itemsList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, AdapterItem.RecycleViewHolder.class);
    }

    public RecycleViewAdapter(Context ctx, AdapterDataSet<AdapterItem> itemsList, int itemLayout) {
        this(ctx, itemsList, itemLayout, AdapterItem.RecycleViewHolder.class);
    }

    public RecycleViewAdapter(Context ctx, AdapterDataSet<AdapterItem> itemsList, int itemLayout, AdapterItem.RecycleViewHolder viewHolderInstance) {
        this(ctx, itemsList, itemLayout, viewHolderInstance.getClass());
    }

    public RecycleViewAdapter(Context ctx, AdapterDataSet<AdapterItem> itemsList, int itemLayout, Class<? extends AdapterItem.RecycleViewHolder> viewHolderClass) {
        this.mItemsList = itemsList;
        this.mItemLayout = itemLayout;
        this.mViewHolderClass = viewHolderClass;
        this.mCountMargin = 0;
        this.mCtx = ctx;
    }

    public AnimationSet getDefaultScrollAnimation() {
        AnimationSet scrollAnimation = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(800);
        scrollAnimation.addAnimation(animation);
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(600);
        scrollAnimation.addAnimation(animation);
        return scrollAnimation;
    }

    public void setGroupScrollAnimation(AnimationSet scrollAnimation) {
        this.mScrollAnimation = scrollAnimation;
    }

    public void resetOriginalList() {
        this.mOriginalList = new AdapterDataSet<AdapterItem>();
        this.mOriginalList.addAll(this.mItemsList);
    }

    public void setClickListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    public void setTouchListener(TouchListener listener) {
        this.mTouchListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterItem.RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View itemView = inflater.inflate(this.mItemLayout, parent, false);

        AdapterItem.RecycleViewHolder viewHolder = new AdapterItem.RecycleViewHolder(itemView);
        Constructor<? extends AdapterItem.RecycleViewHolder> ctor = null;
        try {
            ctor = this.mViewHolderClass.getDeclaredConstructor(View.class);
            ctor.setAccessible(true);
            viewHolder = ctor.newInstance(itemView);
        } catch (NoSuchMethodException x) {
            x.printStackTrace();
        } catch (InstantiationException x) {
            x.printStackTrace();
        } catch (InvocationTargetException x) {
            x.printStackTrace();
        } catch (IllegalAccessException x) {
            x.printStackTrace();
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterItem.RecycleViewHolder viewHolder, int position) {
        viewHolder.setLayoutTag(position);
        if (this.mClickListener != null) {
            viewHolder.setClickListener(this.mClickListener);
        }
        if (this.mTouchListener != null) {
            viewHolder.setTouchListener(this.mTouchListener);
        }
        if (position < this.mItemsList.size()) {
            AdapterItem item = this.mItemsList.get(position);
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, position, item);
            }
        }
    }

    public void setCountMargin(int countMargin){
        if (countMargin <= 0) {
            countMargin = 0;
        }
        this.mCountMargin = countMargin;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (this.mItemsList == null) {
            return 0;
        }
        int count = this.mItemsList.size();
        return count > 0 ? count - this.mCountMargin : count;
    }

    public interface ClickListener extends AdapterItem.RecycleViewHolder.ClickListener {
        @Override
        public void onClick(View view);
    }

    public interface TouchListener extends AdapterItem.RecycleViewHolder.TouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent mv);
    }
}


