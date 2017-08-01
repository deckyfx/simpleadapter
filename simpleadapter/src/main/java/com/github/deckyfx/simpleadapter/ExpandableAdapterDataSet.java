package com.github.deckyfx.simpleadapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Created by decky on 5/21/15.
 */
public class ExpandableAdapterDataSet<E extends AdapterGroupItem, T extends BaseItem> extends ArrayList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
    public ExpandableAdapterDataSet<E, T> find(CharSequence constraint) {
        ExpandableAdapterDataSet<E, T> results = new ExpandableAdapterDataSet<E, T>();
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


    public <T> E get(int i, boolean f) {
        return super.get(i);
    }

    @Override
    public int size() {
        return super.size();
    }
}
