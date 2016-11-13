package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class SimpleAdapter<E extends AdapterItem> extends android.widget.ArrayAdapter implements Serializable, Filterable {
    private AdapterDataSet<E> mItemsList, mBackupList;
    private int mItemLayout;
    private Context mCtx;
    private Class mViewHolderClass;
    private ClickListener mClickListener;
    private TouchListener mTouchListener;
    private Filter mFilter;
    private AnimationSet mScrollAnimation;
    private Object mTag;

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList) {
        this(ctx, itemsList, DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, AdapterItem.ViewHolder.class);
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList, Class<? extends AdapterItem.ViewHolder> viewHolderClass) {
        this(ctx, itemsList, DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, viewHolderClass);
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout, AdapterItem.ViewHolder viewHolderInstance) {
        this(ctx, itemsList, itemLayout, viewHolderInstance.getClass());
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout, Class<? extends AdapterItem.ViewHolder> viewHolderClass) {
        super(ctx, itemLayout, itemsList);
        this.mItemsList = itemsList;
        this.mItemLayout = itemLayout;
        this.mCtx = ctx;
        this.mViewHolderClass = viewHolderClass;
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

    private AdapterItem.ViewHolder initViewHolder(View convertView, Class<? extends AdapterItem.ViewHolder> vhClass, int fallbackLayout) {
        AdapterItem.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = ((LayoutInflater) this.mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(fallbackLayout, null);
        }
        // well set up the ViewHolder
        viewHolder = new AdapterItem.ViewHolder(convertView);
        Constructor<? extends AdapterItem.ViewHolder> ctor = null;
        try {
            ctor = vhClass.getDeclaredConstructor(View.class);
            ctor.setAccessible(true);
            viewHolder = ctor.newInstance(convertView);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        AdapterItem.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = this.initViewHolder(convertView, this.mViewHolderClass, this.mItemLayout);
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            Object tag = convertView.getTag();
            if (tag instanceof AdapterItem.ViewHolder) {
                viewHolder = (AdapterItem.ViewHolder) convertView.getTag();
            } else {
                viewHolder = this.initViewHolder(convertView, this.mViewHolderClass, this.mItemLayout);
                convertView.setTag(viewHolder);
            }
        }
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

    @Override
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new AdapterFilter();
        }
        return this.mFilter;
    }

    public void setClickListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    public void setTouchListener(TouchListener listener) {
        this.mTouchListener = listener;
    }

    public interface ClickListener extends AdapterItem.ViewHolder.ClickListener {
        @Override
        public void onClick(View view);
    }

    public interface TouchListener extends AdapterItem.ViewHolder.TouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent mv);
    }

    public static final class DEFAULT_LIST_VIEW {
        public static final int SIMPLE_LIST_ITEM_1 = android.R.layout.simple_list_item_1;
        public static final int SIMPLE_LIST_ITEM_2 = android.R.layout.simple_list_item_2;
        public static final int SIMPLE_SPINER_ITEM = android.R.layout.simple_spinner_item;
        public static final int SIMPLE_DROPDOWN_ITEM_1LINE = android.R.layout.simple_dropdown_item_1line;
        public static final int SIMPLE_EXPANDABLE_LIST_ITEM_1 = android.R.layout.simple_expandable_list_item_1;
        public static final int SIMPLE_EXPANDABLE_LIST_ITEM_2 = android.R.layout.simple_expandable_list_item_2;
        public static final int SIMPLE_LIST_ITEM_ACTIVATED_1 = android.R.layout.simple_list_item_activated_1;
        public static final int SIMPLE_LIST_ITEM_ACTIVATED_2 = android.R.layout.simple_list_item_activated_2;
        public static final int SIMPLE_LIST_ITEM_CHECKED = android.R.layout.simple_list_item_checked;
        public static final int SIMPLE_LIST_ITEM_MULTIPLE_CHOICE = android.R.layout.simple_list_item_multiple_choice;
        public static final int SIMPLE_LIST_ITEM_SINGLE_CHOICE = android.R.layout.simple_list_item_single_choice;
        public static final int SIMPLE_SELECTABLE_LIST_ITEM = android.R.layout.simple_selectable_list_item;
        public static final int SIMPLE_SPINNER_DROPDOWN_ITEM = android.R.layout.simple_spinner_dropdown_item;
    }

    private class AdapterFilter extends Filter {
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