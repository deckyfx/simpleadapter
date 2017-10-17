package com.github.deckyfx.simpleadapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * Created by decky on 5/21/15.
 */


public class AdapterDataSet<T extends BaseItem> extends ArrayList<T> implements List<T>, RandomAccess, Cloneable, Serializable {
    @Override
    public T get(int i) {
        return super.get(i);
    }

    @Override
    public int size() {
        return super.size();
    }

    public AdapterDataSet<T> filter(ItemTester.Test<T, Boolean> tester, Object object) {
        AdapterDataSet<T> result = new AdapterDataSet<T>();
        for (T element : this) {
            Object r_o = tester.apply(element, object, 0);
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

    public Object testAt(ItemTester.Test<T, ?> tester, int position, Object param) {
        return tester.apply(this.get(position), position, param);
    }
}
