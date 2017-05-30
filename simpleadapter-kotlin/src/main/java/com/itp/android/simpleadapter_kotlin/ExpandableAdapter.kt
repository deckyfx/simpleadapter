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
import android.widget.BaseExpandableListAdapter
import android.widget.Filter
import android.widget.Filterable

import java.io.Serializable
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

class ExpandableAdapter<E : AdapterGroupItem<*>, T : AdapterItem>(private val mCtx: Context, private var mGroupList: ExpandableAdapterDataSet<E, T>?, private val mGroupLayout: Int,
                                                                  private val mChildLayout: Int, private val mGroupViewHolderClass: Class<out AdapterItem.ViewHolder>,
                                                                  private val mChildViewHolderClass: Class<out AdapterItem.ViewHolder>) : BaseExpandableListAdapter(), Serializable, Filterable {
    private var mBackupGroupList: ExpandableAdapterDataSet<E, T>? = null
    private var mClickListener: ClickListener? = null
    private var mTouchListener: TouchListener? = null
    private var mFilter: Filter? = null
    private var mGroupScrollAnimation: AnimationSet? = null
    private var mChildScrollAnimation: AnimationSet? = null
    var tag: Any? = null
    private var mGroupCountMargin: Int = 0
    private var mChildrenCountMargin: Int = 0

    constructor(ctx: Context, groupList: ExpandableAdapterDataSet<E, T>) : this(ctx, groupList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_EXPANDABLE_LIST_ITEM_1,
            SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
            AdapterItem.ViewHolder::class.java,
            AdapterItem.ViewHolder::class.java) {
    }

    constructor(ctx: Context, groupList: ExpandableAdapterDataSet<E, T>, groupViewHolderClass: Class<out AdapterItem.ViewHolder>) : this(ctx, groupList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
            SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
            groupViewHolderClass,
            AdapterItem.ViewHolder::class.java) {
    }

    constructor(ctx: Context, groupList: ExpandableAdapterDataSet<E, T>, viewHolderClass: Class<out AdapterItem.ViewHolder>,
                childViewHolderClass: Class<out AdapterItem.ViewHolder>) : this(ctx, groupList, SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
            SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1,
            viewHolderClass,
            childViewHolderClass) {
    }

    constructor(ctx: Context, itemList: ExpandableAdapterDataSet<E, T>, groupLayout: Int,
                childLayout: Int, groupViewHolderClass: AdapterItem.ViewHolder,
                childViewHolderClass: AdapterItem.ViewHolder) : this(ctx, itemList, groupLayout, childLayout, groupViewHolderClass.javaClass, childViewHolderClass.javaClass) {
    }

    init {
        this.mChildrenCountMargin = 0
        this.mGroupCountMargin = this.mChildrenCountMargin
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
        this.mGroupScrollAnimation = scrollAnimation
    }

    fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        return this.getGroupView(position, false, convertView, parent)
    }

    fun setChildScrollAnimation(scrollAnimation: AnimationSet) {
        this.mChildScrollAnimation = scrollAnimation
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

    fun setGroupCountMargin(countMargin: Int) {
        var countMargin = countMargin
        if (countMargin <= 0) {
            countMargin = 0
        }
        this.mGroupCountMargin = countMargin
    }

    fun setChildrenCountMargin(countMargin: Int) {
        var countMargin = countMargin
        if (countMargin <= 0) {
            countMargin = 0
        }
        this.mChildrenCountMargin = countMargin
    }

    override fun getGroupCount(): Int {
        if (this.mGroupList == null) {
            return 0
        }
        val count = this.mGroupList!!.size
        return if (count > 0) count - this.mGroupCountMargin else count
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        if (this.mGroupList == null) {
            return 0
        }
        val count = this.mGroupList!![groupPosition].childrens.size
        return if (count > 0) count - this.mChildrenCountMargin else count
    }

    override fun getGroup(groupPosition: Int): E? {
        return this.mGroupList!![groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): T? {
        return this.mGroupList!![groupPosition].childrens[childPosition] as T
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val viewHolder: AdapterItem.ViewHolder?
        if (convertView == null) {
            viewHolder = this.initViewHolder(convertView, this.mGroupViewHolderClass, this.mGroupLayout)
            convertView!!.tag = viewHolder
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            val tag = convertView.tag
            if (tag is AdapterItem.ViewHolder) {
                viewHolder = convertView.tag as AdapterItem.ViewHolder
            } else {
                viewHolder = this.initViewHolder(convertView, this.mGroupViewHolderClass, this.mGroupLayout)
                convertView.tag = viewHolder
            }
        }
        if (this.mClickListener != null) {
            viewHolder.setClickListener(this.mClickListener!!)
        }
        if (this.mTouchListener != null) {
            viewHolder.setTouchListener(this.mTouchListener!!)
        }
        if (groupPosition < this.groupCount) {
            val item = this.getGroup(groupPosition)
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, groupPosition, item)
            }
        }
        if (this.mGroupScrollAnimation != null) {
            convertView.startAnimation(this.mGroupScrollAnimation)
        }
        return convertView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val viewHolder: AdapterItem.ViewHolder?
        if (convertView == null) {
            viewHolder = this.initViewHolder(convertView, this.mChildViewHolderClass, this.mChildLayout)
            convertView!!.tag = viewHolder
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            val tag = convertView.tag
            if (tag is AdapterItem.ViewHolder) {
                viewHolder = convertView.tag as AdapterItem.ViewHolder
            } else {
                viewHolder = this.initViewHolder(convertView, this.mChildViewHolderClass, this.mChildLayout)
                convertView.tag = viewHolder
            }
        }
        if (this.mClickListener != null) {
            viewHolder.setClickListener(this.mClickListener!!)
        }
        if (this.mTouchListener != null) {
            viewHolder.setTouchListener(this.mTouchListener!!)
        }
        if (groupPosition < this.groupCount && childPosition < this.getChildrenCount(groupPosition)) {
            val item = this.getChild(groupPosition, childPosition)
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, groupPosition, childPosition, item)
            }
        }
        if (this.mChildScrollAnimation != null) {
            convertView.startAnimation(this.mChildScrollAnimation)
        }
        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    fun resetOriginalList() {
        if (this.mBackupGroupList == null) {
            return
        }
        this.mGroupList = ExpandableAdapterDataSet<E, T>()
        this.mGroupList!!.addAll(this.mBackupGroupList!!)
    }

    fun backupList() {
        this.mBackupGroupList = ExpandableAdapterDataSet<E, T>()
        this.mBackupGroupList!!.addAll(this.mGroupList!!)
    }

    override fun getFilter(): Filter {
        if (this.mFilter == null) {
            this.mFilter = CustomFilter()
        }
        return this.mFilter as Filter
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

    private inner class CustomFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            var constraint = constraint
            constraint = constraint!!.toString().toLowerCase()
            val result = Filter.FilterResults()
            if (constraint != null && constraint.toString().length > 0) {
                val filteredItems = mBackupGroupList!!.find(constraint)
                result.count = filteredItems.size
                result.values = filteredItems
            } else {
                synchronized(this) {
                    result.values = mGroupList
                    result.count = mGroupList!!.size
                }
            }
            return result
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            if (results.count > 0) {
                val result_list = results.values as ExpandableAdapterDataSet<E, T>
                mGroupList!!.removeAll(mGroupList as ExpandableAdapterDataSet<E, T>)
                notifyDataSetChanged()
                mGroupList!!.addAll(result_list)
                notifyDataSetInvalidated()
            }
        }
    }
}
