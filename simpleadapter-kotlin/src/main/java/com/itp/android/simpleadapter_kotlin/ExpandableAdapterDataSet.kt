package com.itp.android.simpleadapter_kotlin

import java.io.Serializable
import java.util.ArrayList
import java.util.RandomAccess

/**
 * Created by decky on 5/21/15.
 */
class ExpandableAdapterDataSet<E : AdapterGroupItem<*>, T : AdapterItem> : ArrayList<E>(), List<E>, RandomAccess, Cloneable, Serializable {
    fun find(constraint: CharSequence): ExpandableAdapterDataSet<E, T> {
        val results = ExpandableAdapterDataSet<E, T>()
        for (i in 0..this.size - 1) {
            if (this[i].testFilter(constraint)) {
                results.add(this[i])
            }
        }
        return results
    }

    override fun get(i: Int): E {
        return super.get(i)
    }


    operator fun <T> get(i: Int, f: Boolean): E {
        return super.get(i)
    }

    override fun clone(): Any {
        return super<ArrayList>.clone();
    }
}
