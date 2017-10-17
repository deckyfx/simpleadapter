package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by decky on 8/3/16.
 */
public class RecycleAdapter<T extends BaseItem> extends RecyclerView.Adapter<AdapterItem.RecycleViewHolder> implements Serializable {
    private AdapterDataSet<T> mItemsList;
    private ArrayList<ViewHolderData> mViewHolders;
    private Context mCtx;
    private SimpleAdapter.ClickListener mClickListener;
    private SimpleAdapter.TouchListener mTouchListener;
    private ViewBindListener mViewBindListener;
    private int mCountMargin;
    private Object mTag;
    private AnimationSet mScrollAnimation;
    private ItemTester.Test<T, Integer> mTypeTester;
    private ItemTester.Test<T, Boolean> mEnableTester;

    public RecycleAdapter(Context ctx, AdapterDataSet<T> itemsList) {
        this(ctx, itemsList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, AdapterItem.RecycleViewHolder.class);
    }

    public RecycleAdapter(Context ctx, AdapterDataSet<T> itemsList, int itemLayout) {
        this(ctx, itemsList, itemLayout, AdapterItem.RecycleViewHolder.class);
    }

    public RecycleAdapter(Context ctx, AdapterDataSet<T> itemsList, int itemLayout, AdapterItem.RecycleViewHolder viewHolderInstance) {
        this(ctx, itemsList, itemLayout, viewHolderInstance.getClass());
    }

    public RecycleAdapter(Context ctx, AdapterDataSet<T> itemsList, int itemLayout, Class<? extends AdapterItem.RecycleViewHolder> viewHolderClass) {
        this.mItemsList = itemsList;
        this.mCountMargin = 0;
        this.mCtx = ctx;
        this.addViewHolder(itemLayout, viewHolderClass);
    }

    public void setOnClickListener(SimpleAdapter.ClickListener listener) {
        this.mClickListener = listener;
    }

    public void setOnTouchListener(SimpleAdapter.TouchListener listener) {
        this.mTouchListener = listener;
    }

    public void setOnViewBindListener(ViewBindListener listener) {
        this.mViewBindListener = listener;
    }

    public void setCountMargin(int countMargin){
        if (countMargin <= 0) {
            countMargin = 0;
        }
        this.mCountMargin = countMargin;
    }

    public void addViewHolder(int layoutId, @Nullable  Class<? extends AdapterItem.RecycleViewHolder> viewHolderClass){
        this.mViewHolders.add(new ViewHolderData(layoutId, viewHolderClass));
    }

    public void setTypeTester(ItemTester.Test<T, Integer> tester) {
        this.mTypeTester = tester;
    }

    public void setEnableTester(ItemTester.Test<T, Boolean> tester) {
        this.mEnableTester = tester;
    }

    public Object getTag() {
        return this.mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public T getItem(int i) {
        return this.mItemsList.get(i);
    }

     public int getViewTypeCount(){
        return this.mViewHolders.size();
    }

    @Override
    public int getItemViewType(int position){
        if (this.mTypeTester == null) return 0;
        Object r_o = this.mItemsList.testAt(this.mTypeTester, position);
        if (r_o instanceof Integer) {
            Integer r_i = (Integer) r_o;
            return r_i;
        } else {
            throw new RuntimeException("To determine item view, Tester implementation have to return Integer value");
        }
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        if (this.mEnableTester == null) return true;
        Object r_o = this.mItemsList.testAt(this.mEnableTester, position);
        if (r_o instanceof Boolean) {
            Boolean r_b = (Boolean) r_o;
            return r_b;
        } else {
            throw new RuntimeException("To determine view enabled, Tester implementation have to return Boolean value");
        }
    }

    public AnimationSet createDefaultScrollAnimation() {
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

    public AnimationSet getScrollAnimation() {
        return this.mScrollAnimation;
    }

    public void setScrollAnimation(AnimationSet scrollAnimation) {
        this.mScrollAnimation = scrollAnimation;
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

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterItem.RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolderData vhData   = this.mViewHolders.get(viewType);
        Context context         = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View itemView           = inflater.inflate(vhData.layout, parent, false);
        AdapterItem.RecycleViewHolder viewHolder = new AdapterItem.RecycleViewHolder(itemView);
        Constructor<? extends AdapterItem.RecycleViewHolder> ctor = null;
        try {
            ctor = vhData.klas.getDeclaredConstructor(View.class);
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
            viewHolder.setOnClickListener(this.mClickListener);
        }
        if (this.mTouchListener != null) {
            viewHolder.setOnTouchListener(this.mTouchListener);
        }
        if (position < this.mItemsList.size()) {
            T item = this.mItemsList.get(position);
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, position, item);
                if (this.mViewBindListener != null) {
                    this.mViewBindListener.onViewBind(this, position);
                }
            }
        }
    }

    public interface ViewBindListener {
        public boolean onViewBind(RecycleAdapter adapter, int position);
    }

    public static class ViewHolderData {
        private Class<? extends AdapterItem.RecycleViewHolder> klas;
        private int layout;

        public ViewHolderData(int layoutId, @Nullable Class<? extends AdapterItem.RecycleViewHolder> viewHolderClass){
            if (layoutId <= 0) {
                layoutId = SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1;
            }
            if (viewHolderClass == null) {
                viewHolderClass = AdapterItem.RecycleViewHolder.class;
            }
            this.layout = layoutId;
            this.klas = viewHolderClass;
        }
    }
}


