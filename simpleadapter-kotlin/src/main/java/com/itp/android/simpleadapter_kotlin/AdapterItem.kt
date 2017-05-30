package com.itp.android.simpleadapter_kotlin

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.widget.CheckedTextView
import android.widget.TextView

open class AdapterItem @JvmOverloads constructor(text: String = "", text2: String = "", var data: Any? = null):BaseItem() {

    var text = ""
    var text2 = ""

    constructor(text: String, data: Any) : this(text, "", data) {}

    init {
        this.text = text
        this.text2 = text2
    }

    // Special method for test filter
    fun testFilter(term: CharSequence): Boolean {
        return this.text.toLowerCase().contains(term.toString().toLowerCase()) || this.text2.toLowerCase().contains(term.toString().toLowerCase())
    }

    class ViewHolder {
        protected var mConvertView: View? = null
        protected var mTextView1: TextView? = null
        protected var mTextView2: TextView? = null
        protected var mCheckedTextView1: CheckedTextView? = null
        protected var mCheckedTextView2: CheckedTextView? = null
        protected var mClickListener: ClickListener? = null
        protected var mTouchListener: TouchListener? = null
        protected var mLayoutTag: Int = 0

        constructor() {

        }

        constructor(convertView: View) {
            this.mConvertView = convertView
            var view = convertView.findViewById(android.R.id.text1)
            if (view is CheckedTextView) {
                this.mCheckedTextView1 = view
            } else if (view is TextView) {
                this.mTextView1 = view
            }
            view = convertView.findViewById(android.R.id.text2)
            if (view is CheckedTextView) {
                this.mCheckedTextView2 = view
            } else if (view is TextView) {
                this.mTextView2 = view
            }
        }

        fun setupView(ctx: Context, position: Int, itemobject: AdapterItem) {
            this.setupView(ctx, position, 0, itemobject)
        }

        fun setupView(ctx: Context, groupPosition: Int, childPosition: Int, itemobject: AdapterItem?) {
            if (itemobject != null) {
                if (this.mTextView1 != null) {
                    this.mTextView1!!.text = itemobject.text
                    this.mTextView1!!.tag = groupPosition
                }
                if (this.mTextView2 != null) {
                    this.mTextView2!!.text = itemobject.text2
                    this.mTextView2!!.tag = groupPosition
                }
                if (this.mCheckedTextView1 != null) {
                    this.mCheckedTextView1!!.text = itemobject.text
                    this.mCheckedTextView1!!.tag = groupPosition
                }
                if (this.mCheckedTextView2 != null) {
                    this.mCheckedTextView2!!.text = itemobject.text2
                    this.mCheckedTextView2!!.tag = groupPosition
                }
            }
        }

        fun setClickListener(clickListener: ClickListener) {
            this.mClickListener = clickListener
        }

        fun setTouchListener(touchListener: TouchListener) {
            this.mTouchListener = touchListener
        }

        interface ClickListener : View.OnClickListener {
            override fun onClick(view: View)
        }

        interface TouchListener : View.OnTouchListener {
            override fun onTouch(view: View, mv: MotionEvent): Boolean
        }
    }

    class RecycleViewHolder : RecyclerView.ViewHolder {
        protected var mConvertView: View? = null
        protected var mTextView1: TextView? = null
        protected var mTextView2: TextView? = null
        protected var mCheckedTextView1: CheckedTextView? = null
        protected var mCheckedTextView2: CheckedTextView? = null
        protected var mClickListener: ClickListener? = null
        protected var mTouchListener: TouchListener? = null
        protected var mLayoutTag: Int = 0

        constructor() : super(null!!) {}

        constructor(convertView: View) : super(null!!) {
            this.mConvertView = convertView
            this.mTextView1 = convertView.findViewById(android.R.id.text1) as TextView
            this.mTextView2 = convertView.findViewById(android.R.id.text2) as TextView
            this.mCheckedTextView1 = convertView.findViewById(android.R.id.text1) as CheckedTextView
            this.mCheckedTextView2 = convertView.findViewById(android.R.id.text2) as CheckedTextView
        }

        fun setupView(ctx: Context, position: Int, itemobject: AdapterItem) {
            this.setupView(ctx, position, 0, itemobject)
        }

        fun setupView(ctx: Context, groupPosition: Int, childPosition: Int, itemobject: AdapterItem?) {
            if (itemobject != null) {
                if (this.mTextView1 != null) {
                    this.mTextView1!!.text = itemobject.text
                    this.mTextView1!!.tag = groupPosition
                }
                if (this.mTextView2 != null) {
                    this.mTextView2!!.text = itemobject.text2
                    this.mTextView2!!.tag = groupPosition
                }
                if (this.mCheckedTextView1 != null) {
                    this.mCheckedTextView1!!.text = itemobject.text
                    this.mCheckedTextView1!!.tag = groupPosition
                }
                if (this.mCheckedTextView2 != null) {
                    this.mCheckedTextView2!!.text = itemobject.text2
                    this.mCheckedTextView2!!.tag = groupPosition
                }
            }
        }

        fun setClickListener(clickListener: ClickListener) {
            this.mClickListener = clickListener
        }

        fun setTouchListener(touchListener: TouchListener) {
            this.mTouchListener = touchListener
        }

        fun setLayoutTag(layoutTag: Int) {
            this.mConvertView?.tag = layoutTag
            this.mLayoutTag = layoutTag
        }

        interface ClickListener : View.OnClickListener {
            override fun onClick(view: View)
        }

        interface TouchListener : View.OnTouchListener {
            override fun onTouch(view: View, mv: MotionEvent): Boolean
        }
    }
}
