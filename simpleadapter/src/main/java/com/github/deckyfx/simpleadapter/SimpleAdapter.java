package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.io.Serializable;

public class SimpleAdapter<E extends BaseItem> extends ArrayAdapter implements Serializable, Filterable {
    private AdapterDataSet<E> mItemsList, mBackupList;
    private int mItemLayout;
    private Context mCtx;
    private Class mViewHolderClass;
    private OnViewBindListener mItemViewBindListener;
    private Filter mFilter;
    private Object mTag;
    private int mCountMargin;
    private AnimationSet mScrollAnimation;

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList) {
        this(ctx, itemsList, AdapterUtil.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, DefaultViewHolder.class);
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout) {
        this(ctx, itemsList, itemLayout, DefaultViewHolder.class);
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList, Class<? extends AbstractViewHolder> viewHolderClass) {
        this(ctx, itemsList, AdapterUtil.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, viewHolderClass);
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout, AbstractViewHolder<E> viewHolderInstance) {
        this(ctx, itemsList, itemLayout, viewHolderInstance.getClass());
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout, Class<? extends AbstractViewHolder> viewHolderClass) {
        super(ctx, itemLayout, itemsList);
        this.mItemsList = itemsList;
        this.mItemLayout = itemLayout;
        this.mCtx = ctx;
        this.mViewHolderClass = viewHolderClass;
        this.mCountMargin = 0;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getView(position, convertView, parent);
    }

    public Object getTag() {
        return this.mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public E getItem(int i) {
        return (E) this.mItemsList.get(i);
    }

    private AbstractViewHolder<E> initViewHolder(View convertView, Class<? extends AbstractViewHolder<E>> vhClass, int fallbackLayout) {
        if (convertView == null) {
            convertView = ((LayoutInflater) this.mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(fallbackLayout, null);
        }
        AbstractViewHolder viewHolder = AdapterUtil.createViewHolderInstance(vhClass, convertView);
        return viewHolder;
    }

    public void setCountMargin(int countMargin){
        if (countMargin <= 0) {
            countMargin = 0;
        }
        this.mCountMargin = countMargin;
    }

    @Override
    public int getCount(){
        if (this.mItemsList == null) {
            return 0;
        }
        int count = this.mItemsList.size();
        count = (count > 0)? (count - this.mCountMargin) : count;
        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AbstractViewHolder viewHolder;
        if (convertView == null) {
            convertView = ((LayoutInflater) this.mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(mItemLayout, null);
            viewHolder = this.initViewHolder(convertView, this.mViewHolderClass, this.mItemLayout);
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            Object tag = convertView.getTag();
            if (tag instanceof AbstractViewHolder) {
                viewHolder = (AbstractViewHolder) convertView.getTag();
            } else {
                viewHolder = this.initViewHolder(convertView, this.mViewHolderClass, this.mItemLayout);
                convertView.setTag(viewHolder);
            }
        }
        if (position < this.mItemsList.size()) {
            E item = this.mItemsList.get(position);
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, -1, position, item);
                if (this.mItemViewBindListener != null) {
                    this.mItemViewBindListener.onViewBind(this, viewHolder, position);
                }
            }
        }
        if (this.mScrollAnimation != null) {
            convertView.startAnimation(this.mScrollAnimation);
        }
        return convertView;
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

    public AnimationSet getScrollAnimation() {
        return this.mScrollAnimation;
    }

    public void setScrollAnimation(AnimationSet scrollAnimation) {
        this.mScrollAnimation = scrollAnimation;
    }

    @Override
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new AdapterFilter();
        }
        return this.mFilter;
    }

    public void setOnItemViewBindListener(OnViewBindListener listener) {
        this.mItemViewBindListener = listener;
    }

    public interface OnViewBindListener {
        void onViewBind(SimpleAdapter adapter, AbstractViewHolder vh, int position);
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
                clear();
                mItemsList.addAll(result_list);
                notifyDataSetInvalidated();
            }
        }
    }
}