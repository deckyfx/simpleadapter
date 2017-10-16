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
    private ObjectTester<T> tester;

    @Override
    public T get(int i) {
        return super.get(i);
    }

    @Override
    public int size() {
        return super.size();
    }

    public AdapterDataSet<T> find(ObjectTester<BaseItem> tester) {
        AdapterDataSet<T> results = new AdapterDataSet<T>();
        for (T element : this) {
            if (element.filter(tester)) results.add(element);
        }
        return results;
    }

    public AdapterDataSet<T> filter(Object filter){
        if (this.tester == null) {
            return this;
        }
        AdapterDataSet<T> result = new AdapterDataSet<T>();
        for (T element : this) {
            if (this.tester.filter(element, filter)) {
                result.add(element);
            }
        }
        return result;
    }

    public void sort() {
        Collections.sort(this, this.tester);
    }

    public int getType(int position){
        return this.tester.getType(this.get(position), position);
    }

    public void setTester(ObjectTester<T> tester){
        this.tester = tester;
    }
}
