package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseExpandableListAdapter;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ExpandableAdapter<T extends AdapterGroupItem, E extends BaseItem> extends BaseExpandableListAdapter implements Serializable {
    private ExpandableAdapterDataSet<T, E> mGroupList;
    private Context mCtx;
    private ArrayList<SimpleAdapter.ViewHolderData> mGroupViewHolders, mChildViewHolders;
    private SimpleAdapter.ClickListener mClickListener;
    private SimpleAdapter.TouchListener mTouchListener;
    private ViewBindListener mViewBindListener;
    private Object mTag;
    private int mGroupCountMargin, mChildrenCountMargin;
    private AnimationSet mGroupScrollAnimation, mChildScrollAnimation;
    private ItemTester.Test<T, Integer> mGroupTypeTester;
    private ItemTester.Test<T, Boolean> mGroupEnableTester;
    private ItemTester.Test<E, Integer> mChildTypeTester;
    private ItemTester.Test<E, Boolean> mChildEnableTester;

    public ExpandableAdapter(Context ctx, ExpandableAdapterDataSet<T, E> groupList) {
        this(ctx, groupList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_EXPANDABLE_LIST_ITEM_1,
                SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
                AdapterItem.ViewHolder.class,
                AdapterItem.ViewHolder.class);
    }

    public ExpandableAdapter(Context ctx, ExpandableAdapterDataSet<T, E> groupList, Class<? extends AdapterItem.ViewHolder> groupViewHolderClass) {
        this(ctx, groupList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
                SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
                groupViewHolderClass,
                AdapterItem.ViewHolder.class);
    }

    public ExpandableAdapter(Context ctx, ExpandableAdapterDataSet<T, E> groupList, Class<? extends AdapterItem.ViewHolder> viewHolderClass,
                             Class<? extends AdapterItem.ViewHolder> childViewHolderClass) {
        this(ctx, groupList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
                SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
                viewHolderClass,
                childViewHolderClass);
    }

    public ExpandableAdapter(Context ctx, ExpandableAdapterDataSet<T, E> itemList, int groupLayout,
                             int childLayout, AdapterItem.ViewHolder groupViewHolderClass,
                             AdapterItem.ViewHolder childViewHolderClass) {
        this(ctx, itemList, groupLayout, childLayout, groupViewHolderClass.getClass(), childViewHolderClass.getClass());
    }

    public ExpandableAdapter(Context ctx, ExpandableAdapterDataSet<T, E> itemList, int groupLayout,
                             int childLayout, Class<? extends AdapterItem.ViewHolder> groupViewHolderClass,
                             Class<? extends AdapterItem.ViewHolder> childViewHolderClass) {
        this.mGroupList = itemList;
        this.addGroupViewHolder(groupLayout, groupViewHolderClass);
        this.addChildViewHolder(childLayout, childViewHolderClass);
        this.mGroupCountMargin = this.mChildrenCountMargin = 0;
        this.mCtx = ctx;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getGroupView(position, false, convertView, parent);
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

    public void addGroupViewHolder(int layoutId, @Nullable  Class<? extends AdapterItem.ViewHolder> viewHolderClass){
        this.mGroupViewHolders.add(new SimpleAdapter.ViewHolderData(layoutId, viewHolderClass));
    }

    public void addChildViewHolder(int layoutId, @Nullable  Class<? extends AdapterItem.ViewHolder> viewHolderClass){
        this.mChildViewHolders.add(new SimpleAdapter.ViewHolderData(layoutId, viewHolderClass));
    }

    public void setGroupTypeTester(ItemTester.Test<T, Integer> tester) {
        this.mGroupTypeTester = tester;
    }

    public void setChildTypeTester(ItemTester.Test<E, Integer> tester) {
        this.mChildTypeTester = tester;
    }

    public void setGroupEnableTester(ItemTester.Test<T, Boolean> tester) {
        this.mGroupEnableTester = tester;
    }

    public void setChildEnableTester(ItemTester.Test<E, Boolean> tester) {
        this.mChildEnableTester = tester;
    }

    public int getGroupViewTypeCount(){
        return this.mGroupViewHolders.size();
    }

    public int getChildGroupViewTypeCount(){
        return this.mChildViewHolders.size();
    }

    public int getGroupItemViewType(int position){
        if (this.mGroupTypeTester == null) return 0;
        Object r_o = this.mGroupList.testAt(this.mGroupTypeTester, position);
        if (r_o instanceof Integer) {
            Integer r_i = (Integer) r_o;
            return r_i;
        } else {
            throw new RuntimeException("To determine item view, Tester implementation have to return Integer value");
        }
    }

    public int getChildItemViewType(int position, int child){
        if (this.mChildTypeTester == null) return 0;
        Object r_o = this.mGroupList.testChildAt(this.mChildTypeTester, position, child);
        if (r_o instanceof Integer) {
            Integer r_i = (Integer) r_o;
            return r_i;
        } else {
            throw new RuntimeException("To determine item view, Tester implementation have to return Integer value");
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isGroupEnabled(int position) {
        if (this.mGroupEnableTester == null) return true;
        Object r_o = this.mGroupList.testAt(this.mGroupEnableTester, position);
        if (r_o instanceof Boolean) {
            Boolean r_b = (Boolean) r_o;
            return r_b;
        } else {
            throw new RuntimeException("To determine view enabled, Tester implementation have to return Boolean value");
        }
    }

    public boolean isChildEnabled(int position, int child) {
        if (this.mGroupEnableTester == null) return true;
        Object r_o = this.mGroupList.testChildAt(this.mChildEnableTester, position, child);
        if (r_o instanceof Boolean) {
            Boolean r_b = (Boolean) r_o;
            return r_b;
        } else {
            throw new RuntimeException("To determine view enabled, Tester implementation have to return Boolean value");
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return this.isChildEnabled(groupPosition, childPosition);
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

    public AnimationSet getGroupScrollAnimation() {
        return this.mGroupScrollAnimation;
    }

    public void setGroupScrollAnimation(AnimationSet scrollAnimation) {
        this.mGroupScrollAnimation = scrollAnimation;
    }

    public AnimationSet getChildScrollAnimation() {
        return this.mChildScrollAnimation;
    }

    public void setChildScrollAnimation(AnimationSet scrollAnimation) {
        this.mChildScrollAnimation = scrollAnimation;
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
    public T getGroup(int groupPosition) {
        return this.mGroupList.get(groupPosition);
    }

    @Override
    public E getChild(int groupPosition, int childPosition) {
        return (E) this.mGroupList.get(groupPosition).childrens.get(childPosition);
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
        SimpleAdapter.ViewHolderData vHolder = this.mGroupViewHolders.get(this.getGroupItemViewType(groupPosition));
        AdapterItem.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = this.initViewHolder(convertView, vHolder.klas, vHolder.layout);
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            Object tag = convertView.getTag();
            if (tag instanceof AdapterItem.ViewHolder) {
                viewHolder = (AdapterItem.ViewHolder) convertView.getTag();
            } else {
                viewHolder = this.initViewHolder(convertView, vHolder.klas, vHolder.layout);
                convertView.setTag(viewHolder);
            }
        }
        if (this.mClickListener != null) {
            viewHolder.setOnClickListener(this.mClickListener);
        }
        if (this.mTouchListener != null) {
            viewHolder.setOnTouchListener(this.mTouchListener);
        }
        if (groupPosition < this.getGroupCount()) {
            T item = this.getGroup(groupPosition);
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, groupPosition, item);
                if (this.mViewBindListener != null) {
                    this.mViewBindListener.onViewBind(this, groupPosition, -1);
                }
            }
        }
        if (this.mGroupScrollAnimation != null) {
            convertView.startAnimation(this.mGroupScrollAnimation);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        SimpleAdapter.ViewHolderData vHolder = this.mChildViewHolders.get(this.getChildItemViewType(groupPosition, childPosition));
        AdapterItem.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = this.initViewHolder(convertView, vHolder.klas, vHolder.layout);
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            Object tag = convertView.getTag();
            if (tag instanceof AdapterItem.ViewHolder) {
                viewHolder = (AdapterItem.ViewHolder) convertView.getTag();
            } else {
                viewHolder = this.initViewHolder(convertView, vHolder.klas, vHolder.layout);
                convertView.setTag(viewHolder);
            }
        }
        if (this.mClickListener != null) {
            viewHolder.setOnClickListener(this.mClickListener);
        }
        if (this.mTouchListener != null) {
            viewHolder.setOnTouchListener(this.mTouchListener);
        }
        if (groupPosition < this.getGroupCount() && childPosition < this.getChildrenCount(groupPosition)) {
            E item = this.getChild(groupPosition, childPosition);
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, groupPosition, childPosition, item);
                if (this.mViewBindListener != null) {
                    this.mViewBindListener.onViewBind(this, groupPosition, childPosition);
                }
            }
        }
        if (this.mChildScrollAnimation != null) {
            convertView.startAnimation(this.mChildScrollAnimation);
        }
        return convertView;
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

    public interface ViewBindListener {
        public boolean onViewBind(ExpandableAdapter adapter, int groupPosition, int childPosition);
    }
}
