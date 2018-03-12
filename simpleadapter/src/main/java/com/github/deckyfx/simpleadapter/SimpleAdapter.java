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
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SimpleAdapter<E extends BaseItem> extends ArrayAdapter implements Serializable, Filterable {
    private AdapterDataSet<E> mItemsList, mBackupList;
    private int mItemLayout;
    private Context mCtx;
    private Class mViewHolderClass;
    private ClickListener mClickListener;
    private TouchListener mTouchListener;
    private ViewBindListener mViewBindListener;
    private Filter mFilter;
    private Object mTag;
    private int mCountMargin;
    private AnimationSet mScrollAnimation;

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList) {
        this(ctx, itemsList, DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, DefaultViewHolder.class);
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList, int itemLayout) {
        this(ctx, itemsList, itemLayout, DefaultViewHolder.class);
    }

    public SimpleAdapter(Context ctx, AdapterDataSet<E> itemsList, Class<? extends AbstractViewHolder<E>> viewHolderClass) {
        this(ctx, itemsList, DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, viewHolderClass);
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
        AbstractViewHolder viewHolder = SimpleAdapter.createViewHolderInstance(vhClass, convertView);
        if (viewHolder == null) {
            throw new Error("Failed to initiate View Holder " + vhClass.getCanonicalName());
        }
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

    @Override
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new AdapterFilter();
        }
        return this.mFilter;
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

    public interface ClickListener extends AbstractViewHolder.ClickListener {
        @Override
        public void onClick(View view);
    }

    public interface TouchListener extends AbstractViewHolder.TouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent mv);
    }

    public interface ViewBindListener {
        public boolean onViewBind(SimpleAdapter adapter, int position);
    }

    public static final class DEFAULT_LIST_VIEW {
        public static final int SIMPLE_LIST_ITEM_1                  = android.R.layout.simple_list_item_1;
        public static final int SIMPLE_LIST_ITEM_2                  = android.R.layout.simple_list_item_2;
        public static final int SIMPLE_SPINER_ITEM                  = android.R.layout.simple_spinner_item;
        public static final int SIMPLE_DROPDOWN_ITEM_1LINE          = android.R.layout.simple_dropdown_item_1line;
        public static final int SIMPLE_EXPANDABLE_LIST_ITEM_1       = android.R.layout.simple_expandable_list_item_1;
        public static final int SIMPLE_EXPANDABLE_LIST_ITEM_2       = android.R.layout.simple_expandable_list_item_2;
        public static final int SIMPLE_LIST_ITEM_ACTIVATED_1        = android.R.layout.simple_list_item_activated_1;
        public static final int SIMPLE_LIST_ITEM_ACTIVATED_2        = android.R.layout.simple_list_item_activated_2;
        public static final int SIMPLE_LIST_ITEM_CHECKED            = android.R.layout.simple_list_item_checked;
        public static final int SIMPLE_LIST_ITEM_MULTIPLE_CHOICE    = android.R.layout.simple_list_item_multiple_choice;
        public static final int SIMPLE_LIST_ITEM_SINGLE_CHOICE      = android.R.layout.simple_list_item_single_choice;
        public static final int SIMPLE_SELECTABLE_LIST_ITEM         = android.R.layout.simple_selectable_list_item;
        public static final int SIMPLE_SPINNER_DROPDOWN_ITEM        = android.R.layout.simple_spinner_dropdown_item;
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

    public static AbstractViewHolder createViewHolderInstance(Class<? extends AbstractViewHolder> klas, View itemView) {
        try {
            Constructor<? extends AbstractViewHolder> ctor  = klas.getDeclaredConstructor(View.class);
            return ctor.newInstance( itemView);
        } catch (NoSuchMethodException x) {
            x.printStackTrace();
        } catch (InstantiationException x) {
            x.printStackTrace();
        } catch (InvocationTargetException x) {
            x.printStackTrace();
        } catch (IllegalAccessException x) {
            x.printStackTrace();
        }
        return null;
    }
}