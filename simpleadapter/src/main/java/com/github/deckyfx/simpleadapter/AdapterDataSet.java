package com.github.deckyfx.simpleadapter;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Created by decky on 5/21/15.
 */


public class AdapterDataSet<E extends BaseItem> extends ArrayList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
    public AdapterDataSet() {
        super();
    }

    public AdapterDataSet(Collection<? extends E> c) {
        super(c);
    }

    public AdapterDataSet<E> find(CharSequence constraint) {
        AdapterDataSet<E> results = new AdapterDataSet<E>();
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).testFilter(constraint)) {
                results.add(this.get(i));
            }
        }
        return results;
    }

    @Override
    public E get(int i) {
        return super.get(i);
    }

    @Override
    public int size() {
        return super.size();
    }
}
