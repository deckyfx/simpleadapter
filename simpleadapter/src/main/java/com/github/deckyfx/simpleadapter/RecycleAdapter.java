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
public class RecycleAdapter<E extends BaseItem> extends RecyclerView.Adapter<AdapterItem.RecycleViewHolder>  implements Serializable, Filterable {
    private AdapterDataSet<E> mItemsList, mOriginalList, mBackupList;
    private int mItemLayout;
    private Context mCtx;
    private Class<? extends AdapterItem.RecycleViewHolder> mViewHolderClass;
    private SimpleAdapter.ClickListener mClickListener;
    private SimpleAdapter.TouchListener mTouchListener;
    private ViewBindListener mViewBindListener;
    private Filter mFilter;
    private AnimationSet mScrollAnimation;
    private int mCountMargin;

    public RecycleAdapter(Context ctx, AdapterDataSet<E> itemsList) {
        this(ctx, itemsList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, AdapterItem.RecycleViewHolder.class);
    }

    public RecycleAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout) {
        this(ctx, itemsList, itemLayout, AdapterItem.RecycleViewHolder.class);
    }

    public RecycleAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout, AdapterItem.RecycleViewHolder viewHolderInstance) {
        this(ctx, itemsList, itemLayout, viewHolderInstance.getClass());
    }

    public RecycleAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout, Class<? extends AdapterItem.RecycleViewHolder> viewHolderClass) {
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
            viewHolder.setOnClickListener(this.mClickListener);
        }
        if (this.mTouchListener != null) {
            viewHolder.setOnTouchListener(this.mTouchListener);
        }
        if (position < this.mItemsList.size()) {
            BaseItem item = this.mItemsList.get(position);
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, position, item);
                this.mViewBindListener.onViewBind(this, position);
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

    public interface ViewBindListener {
        public boolean onViewBind(RecycleAdapter adapter, int position);
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


