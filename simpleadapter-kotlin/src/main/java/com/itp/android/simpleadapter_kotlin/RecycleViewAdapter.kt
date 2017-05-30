package com.itp.android.simpleadapter_kotlin

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.Filter

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

/**
 * Created by decky on 8/3/16.
 */
class RecycleViewAdapter @JvmOverloads constructor(private val mCtx: Context, private val mItemsList: AdapterDataSet<AdapterItem>?, private val mItemLayout: Int = SimpleAdapter.DEFAULT_LIST_VIEW.SIMPLE_LIST_ITEM_1, private val mViewHolderClass: Class<out AdapterItem.RecycleViewHolder> = AdapterItem.RecycleViewHolder::class.java) : RecyclerView.Adapter<AdapterItem.RecycleViewHolder>() {
    private var mOriginalList: AdapterDataSet<AdapterItem>? = null
    private var mClickListener: ClickListener? = null
    private var mTouchListener: TouchListener? = null
    private val mFilter: Filter? = null
    private var mScrollAnimation: AnimationSet? = null
    private var mCountMargin: Int = 0

    constructor(ctx: Context, itemsList: AdapterDataSet<AdapterItem>, itemLayout: Int, viewHolderInstance: AdapterItem.RecycleViewHolder) : this(ctx, itemsList, itemLayout, viewHolderInstance.javaClass) {}

    init {
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

    fun resetOriginalList() {
        this.mOriginalList = AdapterDataSet<AdapterItem>()
        this.mOriginalList!!.addAll(this.mItemsList!!)
    }

    fun setClickListener(listener: ClickListener) {
        this.mClickListener = listener
    }

    fun setTouchListener(listener: TouchListener) {
        this.mTouchListener = listener
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterItem.RecycleViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val itemView = inflater.inflate(this.mItemLayout, parent, false)

        var viewHolder = AdapterItem.RecycleViewHolder(itemView)
        var ctor: Constructor<out AdapterItem.RecycleViewHolder>? = null
        try {
            ctor = this.mViewHolderClass.getDeclaredConstructor(View::class.java)
            ctor!!.isAccessible = true
            viewHolder = ctor.newInstance(itemView)
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

    override fun onBindViewHolder(viewHolder: AdapterItem.RecycleViewHolder?, position: Int) {
        viewHolder!!.setLayoutTag(position)
        if (this.mClickListener != null) {
            viewHolder.setClickListener(this.mClickListener!!)
        }
        if (this.mTouchListener != null) {
            viewHolder.setTouchListener(this.mTouchListener!!)
        }
        if (position < this.mItemsList!!.size) {
            val item = this.mItemsList[position]
            if (viewHolder != null && item != null) {
                viewHolder.setupView(this.mCtx, position, item)
            }
        }
    }

    fun setCountMargin(countMargin: Int) {
        var countMargin = countMargin
        if (countMargin <= 0) {
            countMargin = 0
        }
        this.mCountMargin = countMargin
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (this.mItemsList == null) {
            return 0
        }
        val count = this.mItemsList.size
        return if (count > 0) count - this.mCountMargin else count
    }

    interface ClickListener : AdapterItem.RecycleViewHolder.ClickListener {
        override fun onClick(view: View)
    }

    interface TouchListener : AdapterItem.RecycleViewHolder.TouchListener {
        override fun onTouch(view: View, mv: MotionEvent): Boolean
    }
}


