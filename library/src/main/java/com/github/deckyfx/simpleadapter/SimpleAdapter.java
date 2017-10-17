package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class SimpleAdapter<T extends BaseItem> extends ArrayAdapter<T> implements Serializable {
    private AdapterDataSet<T> mItemsList;
    private ArrayList<ViewHolderData> mViewHolders;
    private Context mCtx;
    private ClickListener mClickListener;
    private TouchListener mTouchListener;
    private ViewBindListener mViewBindListener;
    private Object mTag;
    private int mCountMargin;
    private AnimationSet mScrollAnimation;
    private ItemTester.Test<T, Integer> mTypeTester;
    private ItemTester.Test<T, Boolean> mEnableTester;

    public SimpleAdapter(Context ctx, AdapterDataSet<T> itemsList) {
        this(ctx, itemsList, DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, AdapterItem.ViewHolder.class);
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<T> itemsList, Class<? extends AdapterItem.ViewHolder> viewHolderClass) {
        this(ctx, itemsList, DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, viewHolderClass);
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<T> itemsList, int itemLayout, AdapterItem.ViewHolder viewHolderInstance) {
        this(ctx, itemsList, itemLayout, viewHolderInstance.getClass());
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<T> itemsList, int itemLayout, Class<? extends AdapterItem.ViewHolder> viewHolderClass) {
        super(ctx, itemLayout, itemsList);
        this.mItemsList = itemsList;
        this.mCtx = ctx;
        this.mCountMargin = 0;
        this.addViewHolder(itemLayout, viewHolderClass);
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

    public T getItem(int i) {
        return this.mItemsList.get(i);
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

    public void setCountMargin(int countMargin){
        if (countMargin <= 0) {
            countMargin = 0;
        }
        this.mCountMargin = countMargin;
    }

    public void addViewHolder(int layoutId, @Nullable  Class<? extends AdapterItem.ViewHolder> viewHolderClass){
        this.mViewHolders.add(new ViewHolderData(layoutId, viewHolderClass));
    }

    public void setTypeTester(ItemTester.Test<T, Integer> tester) {
        this.mTypeTester = tester;
    }

    public void setEnableTester(ItemTester.Test<T, Boolean> tester) {
        this.mEnableTester = tester;
    }

    @Override
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

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
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
        ViewHolderData vhData = this.mViewHolders.get(this.getItemViewType(position));
        AdapterItem.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = ((LayoutInflater) this.mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(vhData.layout, null);
            viewHolder = this.initViewHolder(convertView, vhData.klas, vhData.layout);
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            Object tag = convertView.getTag();
            if (tag instanceof AdapterItem.ViewHolder) {
                viewHolder = (AdapterItem.ViewHolder) convertView.getTag();
            } else {
                viewHolder = this.initViewHolder(convertView, vhData.klas, vhData.layout);
                convertView.setTag(viewHolder);
            }
        }
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
        if (this.mScrollAnimation != null) {
            convertView.startAnimation(this.mScrollAnimation);
        }
        return convertView;
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

    public void setOnClickListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    public void setOnTouchListener(TouchListener listener) {
        this.mTouchListener = listener;
    }

    public void setOnViewBindListener(ViewBindListener listener) {
        this.mViewBindListener = listener;
    }

    public interface ClickListener extends AdapterItem.ViewHolder.ClickListener {
        @Override
        public void onClick(View view);
    }

    public interface TouchListener extends AdapterItem.ViewHolder.TouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent mv);
    }

    public interface ViewBindListener {
        public boolean onViewBind(SimpleAdapter adapter, int position);
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

    public static class ViewHolderData {
        protected Class<? extends AdapterItem.ViewHolder> klas;
        protected int layout;

        public ViewHolderData(int layoutId, @Nullable  Class<? extends AdapterItem.ViewHolder> viewHolderClass){
            if (layoutId <= 0) {
                layoutId = DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1;
            }
            if (viewHolderClass == null) {
                viewHolderClass = AdapterItem.ViewHolder.class;
            }
            this.layout = layoutId;
            this.klas = viewHolderClass;
        }
    }
}