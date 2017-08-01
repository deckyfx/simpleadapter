package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class AdapterItem extends BaseItem{
    public String text                              = "";
    public String text2                             = "";
    public Object data;

    public AdapterItem() {
        this("", "", null);
    }

    public AdapterItem(String text) {
        this(text, "", null);
    }

    public AdapterItem(String text, Object data) {
        this(text, "", data);
    }

    public AdapterItem(String text, String text2) {
        this(text, text2, null);
    }

    public AdapterItem(String text, String text2, Object data) {
        this.text = text;
        this.text2 = text2;
        this.data = data;
    }

    // Special method for test filter
    public boolean testFilter(CharSequence term) {
        return this.text.toLowerCase().contains(term.toString().toLowerCase()) || this.text2.toLowerCase().contains(term.toString().toLowerCase());
    }

    public static class ViewHolder {
        protected View mConvertView;
        protected TextView mTextView1, mTextView2;
        protected CheckedTextView mCheckedTextView1, mCheckedTextView2;
        protected ClickListener mClickListener;
        protected TouchListener mTouchListener;
        protected int mLayoutTag;

        public ViewHolder() {

        }

        public ViewHolder(View convertView) {
            this.mConvertView = convertView;
            View view = convertView.findViewById(android.R.id.text1);
            if (view instanceof CheckedTextView) {
                this.mCheckedTextView1 = (CheckedTextView) view;
            } else if (view instanceof TextView) {
                this.mTextView1 = (TextView) view;
            }
            view = convertView.findViewById(android.R.id.text2);
            if (view instanceof CheckedTextView) {
                this.mCheckedTextView2 = (CheckedTextView) view;
            } else if (view instanceof TextView) {
                this.mTextView2 = (TextView) view;
            }
        }

        public void setupView(Context ctx, int position, BaseItem itemobject) {
            this.setupView(ctx, position, 0, itemobject);
        }

        public void setupView(Context ctx, int groupPosition, int childPosition, BaseItem itemobject) {
            AdapterItem item = (AdapterItem) itemobject;
            if (item != null) {
                if (this.mTextView1 != null) {
                    this.mTextView1.setText(item.text);
                    this.mTextView1.setTag(groupPosition);
                }
                if (this.mTextView2 != null) {
                    this.mTextView2.setText(item.text2);
                    this.mTextView2.setTag(groupPosition);
                }
                if (this.mCheckedTextView1 != null) {
                    this.mCheckedTextView1.setText(item.text);
                    this.mCheckedTextView1.setTag(groupPosition);
                }
                if (this.mCheckedTextView2 != null) {
                    this.mCheckedTextView2.setText(item.text2);
                    this.mCheckedTextView2.setTag(groupPosition);
                }
            }
        }

        public void setClickListener(ClickListener clickListener) {
            this.mClickListener = clickListener;
        }

        public void setTouchListener(TouchListener touchListener) {
            this.mTouchListener = touchListener;
        }

        public interface ClickListener extends View.OnClickListener {
            @Override
            public void onClick(View view);
        }

        public interface TouchListener extends View.OnTouchListener {
            @Override
            public boolean onTouch(View view, MotionEvent mv);
        }
    }

    public static class RecycleViewHolder extends RecyclerView.ViewHolder {
        protected View mConvertView;
        protected TextView mTextView1, mTextView2;
        protected CheckedTextView mCheckedTextView1, mCheckedTextView2;
        protected AdapterItem.ViewHolder.ClickListener mClickListener;
        protected AdapterItem.ViewHolder.TouchListener mTouchListener;
        protected int mLayoutTag;

        public RecycleViewHolder() {
            super(null);
        }

        public RecycleViewHolder(View convertView) {
            super(null);
            this.mConvertView = convertView;
            this.mTextView1 = (TextView) convertView.findViewById(android.R.id.text1);
            this.mTextView2 = (TextView) convertView.findViewById(android.R.id.text2);
            this.mCheckedTextView1 = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            this.mCheckedTextView2 = (CheckedTextView) convertView.findViewById(android.R.id.text2);
        }

        public void setupView(Context ctx, int position, BaseItem itemobject) {
            this.setupView(ctx, position, 0, itemobject);
        }

        public void setupView(Context ctx, int groupPosition, int childPosition, BaseItem itemobject) {
            AdapterItem item = (AdapterItem) itemobject;
            if (item != null) {
                if (this.mTextView1 != null) {
                    this.mTextView1.setText(item.text);
                    this.mTextView1.setTag(groupPosition);
                }
                if (this.mTextView2 != null) {
                    this.mTextView2.setText(item.text2);
                    this.mTextView2.setTag(groupPosition);
                }
                if (this.mCheckedTextView1 != null) {
                    this.mCheckedTextView1.setText(item.text);
                    this.mCheckedTextView1.setTag(groupPosition);
                }
                if (this.mCheckedTextView2 != null) {
                    this.mCheckedTextView2.setText(item.text2);
                    this.mCheckedTextView2.setTag(groupPosition);
                }
            }
        }

        public void setClickListener(AdapterItem.ViewHolder.ClickListener clickListener) {
            this.mClickListener = clickListener;
        }

        public void setTouchListener(AdapterItem.ViewHolder.TouchListener touchListener) {
            this.mTouchListener = touchListener;
        }

        public void setLayoutTag(int layoutTag) {
            this.mConvertView.setTag(layoutTag);
            this.mLayoutTag = layoutTag;
        }
    }
}
