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
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ExpandableAdapter<E extends AdapterGroupItem, T extends AdapterItem> extends BaseExpandableListAdapter implements Serializable, Filterable {
    private ExpandableAdapterDataSet<E, T> mGroupList, mBackupGroupList;
    private int mGroupLayout, mChildLayout;
    private Context mCtx;
    private Class<? extends AdapterItem.ViewHolder> mGroupViewHolderClass, mChildViewHolderClass;
    private ClickListener mClickListener;
    private TouchListener mTouchListener;
    private Filter mFilter;
    private AnimationSet mGroupScrollAnimation, mChildScrollAnimation;
    private Object mTag;
    private int mGroupCountMargin, mChildrenCountMargin;

    public ExpandableAdapter(Context ctx, ExpandableAdapterDataSet<E, T> groupList) {
        this(ctx, groupList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_EXPANDABLE_LIST_ITEM_1,
                SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
                AdapterItem.ViewHolder.class,
                AdapterItem.ViewHolder.class);
    }

    public ExpandableAdapter(Context ctx, ExpandableAdapterDataSet<E, T> groupList, Class<? extends AdapterItem.ViewHolder> groupViewHolderClass) {
        this(ctx, groupList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
                SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
                groupViewHolderClass,
                AdapterItem.ViewHolder.class);
    }

    public ExpandableAdapter(Context ctx, ExpandableAdapterDataSet<E, T> groupList, Class<? extends AdapterItem.ViewHolder> viewHolderClass,
                             Class<? extends AdapterItem.ViewHolder> childViewHolderClass) {
        this(ctx, groupList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
                SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
                viewHolderClass,
                childViewHolderClass);
    }

    public ExpandableAdapter(Context ctx, ExpandableAdapterDataSet<E, T> itemList, int groupLayout,
                             int childLayout, AdapterItem.ViewHolder groupViewHolderClass,
                             AdapterItem.ViewHolder childViewHolderClass) {
        this(ctx, itemList, groupLayout, childLayout, groupViewHolderClass.getClass(), childViewHolderClass.getClass());
    }

    public ExpandableAdapter(Context ctx, ExpandableAdapterDataSet<E, T> itemList, int groupLayout,
                             int childLayout, Class<? extends AdapterItem.ViewHolder> groupViewHolderClass,
                             Class<? extends AdapterItem.ViewHolder> childViewHolderClass) {
        this.mGroupList = itemList;
        this.mGroupLayout = groupLayout;
        this.mChildLayout = childLayout;
        this.mGroupViewHolderClass = groupViewHolderClass;
        this.mChildViewHolderClass = childViewHolderClass;
        this.mGroupCountMargin = this.mChildrenCountMargin = 0;
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
        this.mGroupScrollAnimation = scrollAnimation;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getGroupView(position, false, convertView, parent);
    }

    public void setChildScrollAnimation(AnimationSet scrollAnimation) {
        this.mChildScrollAnimation = scrollAnimation;
    }

    public Object getTag() {
        return this.mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
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

    public void setGroupCountMargin(int countMargin){
        if (countMargin <= 0) {
            countMargin = 0;
        }
        this.mGroupCountMargin = countMargin;
    }

    public void setChildrenCountMargin(int countMargin){
        if (countMargin <= 0) {
            countMargin = 0;
        }
        this.mChildrenCountMargin = countMargin;
    }

    @Override
    public int getGroupCount() {
        if (this.mGroupList == null) {
            return 0;
        }
        int count = this.mGroupList.size();
        return count > 0 ? count - this.mGroupCountMargin : count;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this.mGroupList == null) {
            return 0;
        }
        int count = this.mGroupList.get(groupPosition).childrens.size();
        return count > 0 ? count - this.mChildrenCountMargin : count;
    }

    @Override
    public E getGroup(int groupPosition) {
        return this.mGroupList.get(groupPosition);
    }

    @Override
    public T getChild(int groupPosition, int childPosition) {
        return (T) this.mGroupList.get(groupPosition).childrens.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        AdapterItem.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = this.initViewHolder(convertView, this.mGroupViewHolderClass, this.mGroupLayout);
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            Object tag = convertView.getTag();
            if (tag instanceof AdapterItem.ViewHolder) {
                viewHolder = (AdapterItem.ViewHolder) convertView.getTag();
            } else {
                viewHolder = this.initViewHolder(convertView, this.mGroupViewHolderClass, this.mGroupLayout);
                convertView.setTag(viewHolder);
            }
        }
        if (this.mClickListener != null) {
            viewHolder.setClickListener(this.mClickListener);
        }
        if (this.mTouchListener != null) {
            viewHolder.setTouchListener(this.mTouchListener);
        }
        if (groupPosition < this.getGroupCount()) {
            AdapterItem item = this.getGroup(groupPosition);
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, groupPosition, item);
            }
        }
        if (this.mGroupScrollAnimation != null) {
            convertView.startAnimation(this.mGroupScrollAnimation);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        AdapterItem.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = this.initViewHolder(convertView, this.mChildViewHolderClass, this.mChildLayout);
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            Object tag = convertView.getTag();
            if (tag instanceof AdapterItem.ViewHolder) {
                viewHolder = (AdapterItem.ViewHolder) convertView.getTag();
            } else {
                viewHolder = this.initViewHolder(convertView, this.mChildViewHolderClass, this.mChildLayout);
                convertView.setTag(viewHolder);
            }
        }
        if (this.mClickListener != null) {
            viewHolder.setClickListener(this.mClickListener);
        }
        if (this.mTouchListener != null) {
            viewHolder.setTouchListener(this.mTouchListener);
        }
        if (groupPosition < this.getGroupCount() && childPosition < this.getChildrenCount(groupPosition)) {
            AdapterItem item = this.getChild(groupPosition, childPosition);
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, groupPosition, childPosition, item);
            }
        }
        if (this.mChildScrollAnimation != null) {
            convertView.startAnimation(this.mChildScrollAnimation);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void resetOriginalList() {
        if (this.mBackupGroupList == null) {
            return;
        }
        this.mGroupList = new ExpandableAdapterDataSet<E, T>();
        this.mGroupList.addAll(this.mBackupGroupList);
    }

    public void backupList() {
        this.mBackupGroupList = new ExpandableAdapterDataSet<E, T>();
        this.mBackupGroupList.addAll(this.mGroupList);
    }

    @Override
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new CustomFilter();
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

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ExpandableAdapterDataSet<E, T> filteredItems = mBackupGroupList.find(constraint);
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = mGroupList;
                    result.count = mGroupList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                ExpandableAdapterDataSet<E, T> result_list = (ExpandableAdapterDataSet<E, T>) results.values;
                mGroupList.removeAll(mGroupList);
                notifyDataSetChanged();
                mGroupList.addAll(result_list);
                notifyDataSetInvalidated();
            }
        }
    }
}
