package com.itp.android.simpleadapter_kotlin

import java.io.Serializable
import java.util.ArrayList
import java.util.RandomAccess

/**
 * Created by decky on 5/21/15.
 */


class AdapterDataSet<E : AdapterItem> : ArrayList<E>(), List<E>, RandomAccess, Cloneable, Serializable {
    fun find(constraint: CharSequence): AdapterDataSet<E> {
        val results = AdapterDataSet<E>()
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

    override fun clone(): Any {
        return super<ArrayList>.clone();
    }
}
