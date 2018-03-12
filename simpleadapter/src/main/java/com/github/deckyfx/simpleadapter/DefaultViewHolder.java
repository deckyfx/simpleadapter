package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class DefaultViewHolder<E extends AdapterItem> extends AbstractViewHolder<E> {
    private TextView mTextView1, mTextView2;
    private CheckedTextView mCheckedTextView1, mCheckedTextView2;

    public DefaultViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
        View view                   = itemView.findViewById(android.R.id.text1);
        if (view instanceof CheckedTextView) {
            this.mCheckedTextView1  = (CheckedTextView) view;
        } else if (view instanceof TextView) {
            this.mTextView1         = (TextView) view;
        }
        view                        = itemView.findViewById(android.R.id.text2);
        if (view instanceof CheckedTextView) {
            this.mCheckedTextView2  = (CheckedTextView) view;
        } else if (view instanceof TextView) {
            this.mTextView2         = (TextView) view;
        }
    } 

    @Override
    public void setupView(Context ctx, int groupPosition, int itemPosition, AdapterItem item) {
        if (item != null) {
            if (this.mTextView1 != null) {
                this.mTextView1.setText(item.text);
                this.mTextView1.setTag(itemPosition);
            }
            if (this.mTextView2 != null) {
                this.mTextView2.setText(item.text2);
                this.mTextView2.setTag(itemPosition);
            }
            if (this.mCheckedTextView1 != null) {
                this.mCheckedTextView1.setText(item.text);
                this.mCheckedTextView1.setTag(itemPosition);
            }
            if (this.mCheckedTextView2 != null) {
                this.mCheckedTextView2.setText(item.text2);
                this.mCheckedTextView2.setTag(itemPosition);
            }
        }
    }
}
