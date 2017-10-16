package com.github.deckyfx.simpleadapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * Created by decky on 5/21/15.
 */
public class ExpandableAdapterDataSet<T extends AdapterGroupItem, E extends BaseItem> extends ArrayList<T> implements List<T>, RandomAccess, Cloneable, Serializable {
    @Override
    public T get(int i) {
        return super.get(i);
    }


    public <E> T get(int i, boolean f) {
        return super.get(i);
    }

    @Override
    public int size() {
        return super.size();
    }

    public ExpandableAdapterDataSet<T, E> filter(ItemTester.Test<T, Boolean> tester, Object object) {
        ExpandableAdapterDataSet<T, E> result = new ExpandableAdapterDataSet<T, E>();
        for (T element : this) {
            Object r_o = tester.apply(element, object);
            if (tester.apply(element, object)) result.add(element);
        }
        return result;
    }

    public void sort(ItemTester.Compare compare) {
        Collections.sort(this, compare);
    }

    public Object testAt(ItemTester.Test<T, ?> tester, int position) {
        return tester.apply(this.get(position), position);
    }

    public Object testChildAt(ItemTester.Test<E, ?> tester, int position, int child) {
        return this.get(position).childrens.testAt(tester, position, child);
    }
}
