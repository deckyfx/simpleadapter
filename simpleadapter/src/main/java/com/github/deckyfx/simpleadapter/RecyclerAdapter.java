package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Filter;
import android.widget.Filterable;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by decky on 8/3/16.
 */
public class RecyclerAdapter<E extends BaseItem> extends RecyclerView.Adapter<AbstractViewHolder<E>> implements Serializable, Filterable {
    private AdapterDataSet<E> mItemsList, mOriginalList, mBackupList;
    private int mItemLayout;
    private Context mCtx;
    private Class<? extends AbstractViewHolder> mViewHolderClass;
    private SimpleAdapter.ClickListener mClickListener;
    private SimpleAdapter.TouchListener mTouchListener;
    private ViewBindListener mViewBindListener;
    private Filter mFilter;
    private int mCountMargin;
    private AnimationSet mScrollAnimation;

    public RecyclerAdapter(Context ctx, AdapterDataSet<E> itemsList) {
        this(ctx, itemsList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, DefaultViewHolder.class);
    }

    public RecyclerAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout) {
        this(ctx, itemsList, itemLayout, DefaultViewHolder.class);
    }

    public RecyclerAdapter(Context ctx, AdapterDataSet<E> itemsList, Class<? extends AbstractViewHolder> viewHolderClass) {
        this(ctx, itemsList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, DefaultViewHolder.class);
    }

    public RecyclerAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout, AbstractViewHolder<E> viewHolderInstance) {
        this(ctx, itemsList, itemLayout, viewHolderInstance.getClass());
    }

    public RecyclerAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout, Class<? extends AbstractViewHolder> viewHolderClass) {
        this.mItemsList = itemsList;
        this.mItemLayout = itemLayout;
        this.mViewHolderClass = viewHolderClass;
        this.mCountMargin = 0;
        this.mCtx = ctx;
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

    // Create new views (invoked by the layout manager)
    @Override
    public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context         = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View itemView           = inflater.inflate(this.mItemLayout, parent, false);
        AbstractViewHolder viewHolder = SimpleAdapter.createViewHolderInstance(this.mViewHolderClass, itemView);
        if (viewHolder == null) {
            throw new Error("Failed to initiate View Holder " + this.mViewHolderClass.getCanonicalName());
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AbstractViewHolder viewHolder, int position) {
        viewHolder.setLayoutTag(position);
        if (this.mClickListener != null) {
            viewHolder.setOnClickListener(this.mClickListener);
        }
        if (this.mTouchListener != null) {
            viewHolder.setOnTouchListener(this.mTouchListener);
        }
        if (position < this.mItemsList.size()) {
            E item = this.mItemsList.get(position);
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, -1, position, item);
                if (this.mViewBindListener != null) {
                    this.mViewBindListener.onViewBind(this, position);
                }
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

    public void resetOriginalList() {
        if (this.mBackupList == null) {
            return;
        }
        this.mItemsList = new AdapterDataSet<E>();
        this.mItemsList.addAll(this.mBackupList);
    }

    public void backupList() {
        this.mBackupList = new AdapterDataSet<E>();
        this.mBackupList.addAll(this.mItemsList);
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

    public interface ViewBindListener {
        public boolean onViewBind(RecyclerAdapter adapter, int position);
    }

    @Override
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new AdapterFilter();
        }
        return this.mFilter;
    }

    public class AdapterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (mBackupList == null) {
                backupList();
            }
            FilterResults result = new FilterResults();
            // If the constraint (search string/pattern) is null
            // or its length is 0, i.e., its empty then
            // we just set the `values` property to the
            // original contacts list which contains all of them
            if (constraint == null || constraint.length() == 0) {
                synchronized (this) {
                    result.values = mBackupList;
                    result.count = mBackupList.size();
                }
            } else {
                synchronized (this) {
                    AdapterDataSet<E> filteredItems = mBackupList.find(constraint);
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                AdapterDataSet<E> result_list = (AdapterDataSet<E>) results.values;
                mItemsList.removeAll(mItemsList);
                notifyDataSetChanged();
                // clear();
                mItemsList.addAll(result_list);
                notifyDataSetChanged();
            }
        }
    }
}


