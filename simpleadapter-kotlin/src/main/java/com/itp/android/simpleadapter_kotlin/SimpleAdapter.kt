package com.itp.android.simpleadapter_kotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.Filter
import android.widget.Filterable

import java.io.Serializable
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

class SimpleAdapter<E : AdapterItem> @JvmOverloads constructor(private val mCtx: Context, private var mItemsList: AdapterDataSet<E>?, private val mItemLayout: Int = SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, viewHolderClass: Class<out AdapterItem.ViewHolder> = AdapterItem.ViewHolder::class.java) : android.widget.ArrayAdapter<E>(mCtx, mItemLayout, mItemsList), Serializable, Filterable {
    private var mBackupList: AdapterDataSet<E>? = null
    private val mViewHolderClass: Class<out AdapterItem.ViewHolder>
    private var mClickListener: ClickListener? = null
    private var mTouchListener: TouchListener? = null
    private var mFilter: Filter? = null
    private var mScrollAnimation: AnimationSet? = null
    var tag: Any? = null
    private var mCountMargin: Int = 0

    constructor(ctx: Context, itemsList: AdapterDataSet<E>, viewHolderClass: Class<out AdapterItem.ViewHolder>) : this(ctx, itemsList, DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, viewHolderClass) {}

    constructor(ctx: Context, itemsList: AdapterDataSet<E>, itemLayout: Int, viewHolderInstance: AdapterItem.ViewHolder) : this(ctx, itemsList, itemLayout, viewHolderInstance.javaClass) {}

    init {
        this.mViewHolderClass = viewHolderClass
        this.mCountMargin = 0
    }

    val defaultScrollAnimation: AnimationSet
        get() {
            val scrollAnimation = AnimationSet(true)
            var animation: Animation = AlphaAnimation(0.0f, 1.0f)
            animation.duration = 800
            scrollAnimation.addAnimation(animation)
            animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f
            )
            animation.setDuration(600)
            scrollAnimation.addAnimation(animation)
            return scrollAnimation
        }

    fun setGroupScrollAnimation(scrollAnimation: AnimationSet) {
        this.mScrollAnimation = scrollAnimation
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return this.getView(position, convertView, parent)
    }

    override fun getItem(i: Int): E? {
        return this.mItemsList!![i]
    }

    private fun initViewHolder(convertView: View?, vhClass: Class<out AdapterItem.ViewHolder>, fallbackLayout: Int): AdapterItem.ViewHolder {
        var convertView = convertView
        var viewHolder: AdapterItem.ViewHolder
        if (convertView == null) {
            convertView = (this.mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(fallbackLayout, null)
        }
        // well set up the ViewHolder
        viewHolder = AdapterItem.ViewHolder(convertView!!)
        var ctor: Constructor<out AdapterItem.ViewHolder>? = null
        try {
            ctor = vhClass.getDeclaredConstructor(View::class.java)
            ctor!!.isAccessible = true
            viewHolder = ctor.newInstance(convertView)
        } catch (x: NoSuchMethodException) {
            x.printStackTrace()
        } catch (x: InstantiationException) {
            x.printStackTrace()
        } catch (x: InvocationTargetException) {
            x.printStackTrace()
        } catch (x: IllegalAccessException) {
            x.printStackTrace()
        }

        return viewHolder
    }

    fun setCountMargin(countMargin: Int) {
        var countMargin = countMargin
        if (countMargin <= 0) {
            countMargin = 0
        }
        this.mCountMargin = countMargin
    }

    override fun getCount(): Int {
        if (this.mItemsList == null) {
            return 0
        }
        val count = this.mItemsList!!.size
        return if (count > 0) count - this.mCountMargin else count
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: AdapterItem.ViewHolder?
        if (convertView == null) {
            convertView = (this.mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(mItemLayout, null)
            viewHolder = this.initViewHolder(convertView, this.mViewHolderClass, this.mItemLayout)
            convertView!!.tag = viewHolder
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            val tag = convertView.tag
            if (tag is AdapterItem.ViewHolder) {
                viewHolder = convertView.tag as AdapterItem.ViewHolder
            } else {
                viewHolder = this.initViewHolder(convertView, this.mViewHolderClass, this.mItemLayout)
                convertView.tag = viewHolder
            }
        }
        if (this.mClickListener != null) {
            viewHolder.setClickListener(this.mClickListener!!)
        }
        if (this.mTouchListener != null) {
            viewHolder.setTouchListener(this.mTouchListener!!)
        }
        if (position < this.mItemsList!!.size) {
            val item = this.mItemsList!![position]
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, position, item)
            }
        }
        if (this.mScrollAnimation != null) {
            convertView.startAnimation(this.mScrollAnimation)
        }
        return convertView
    }

    fun resetOriginalList() {
        if (this.mBackupList == null) {
            return
        }
        this.mItemsList = AdapterDataSet<E>()
        this.mItemsList!!.addAll(this.mBackupList!!)
    }

    fun backupList() {
        this.mBackupList = AdapterDataSet<E>()
        this.mBackupList!!.addAll(this.mItemsList!!)
    }

    override fun getFilter(): Filter {
        if (this.mFilter == null) {
            this.mFilter = AdapterFilter()
        }
        return this.mFilter!!
    }

    fun setClickListener(listener: ClickListener) {
        this.mClickListener = listener
    }

    fun setTouchListener(listener: TouchListener) {
        this.mTouchListener = listener
    }

    interface ClickListener : AdapterItem.ViewHolder.ClickListener {
        override fun onClick(view: View)
    }

    interface TouchListener : AdapterItem.ViewHolder.TouchListener {
        override fun onTouch(view: View, mv: MotionEvent): Boolean
    }

    object DEFAULT_LIST_VIEW {
        val SIMPLE_LIST_ITEM_1 = android.R.layout.simple_list_item_1
        val SIMPLE_LIST_ITEM_2 = android.R.layout.simple_list_item_2
        val SIMPLE_SPINER_ITEM = android.R.layout.simple_spinner_item
        val SIMPLE_DROPDOWN_ITEM_1LINE = android.R.layout.simple_dropdown_item_1line
        val SIMPLE_EXPANDABLE_LIST_ITEM_1 = android.R.layout.simple_expandable_list_item_1
        val SIMPLE_EXPANDABLE_LIST_ITEM_2 = android.R.layout.simple_expandable_list_item_2
        val SIMPLE_LIST_ITEM_ACTIVATED_1 = android.R.layout.simple_list_item_activated_1
        val SIMPLE_LIST_ITEM_ACTIVATED_2 = android.R.layout.simple_list_item_activated_2
        val SIMPLE_LIST_ITEM_CHECKED = android.R.layout.simple_list_item_checked
        val SIMPLE_LIST_ITEM_MULTIPLE_CHOICE = android.R.layout.simple_list_item_multiple_choice
        val SIMPLE_LIST_ITEM_SINGLE_CHOICE = android.R.layout.simple_list_item_single_choice
        val SIMPLE_SELECTABLE_LIST_ITEM = android.R.layout.simple_selectable_list_item
        val SIMPLE_SPINNER_DROPDOWN_ITEM = android.R.layout.simple_spinner_dropdown_item
    }

    private inner class AdapterFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            if (mBackupList == null) {
                backupList()
            }
            val result = Filter.FilterResults()
            // If the constraint (search string/pattern) is null
            // or its length is 0, i.e., its empty then
            // we just set the `values` property to the
            // original contacts list which contains all of them
            if (constraint == null || constraint.length == 0) {
                synchronized(this) {
                    result.values = mBackupList
                    result.count = mBackupList!!.size
                }
            } else {
                synchronized(this) {
                    val filteredItems = mBackupList!!.find(constraint)
                    result.count = filteredItems.size
                    result.values = filteredItems
                }
            }
            return result
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            if (results.count > 0) {
                val result_list = results.values as AdapterDataSet<E>
                mItemsList!!.removeAll(mItemsList!!)
                notifyDataSetChanged()
                clear()
                mItemsList!!.addAll(result_list)
                notifyDataSetInvalidated()
            }
        }
    }
}