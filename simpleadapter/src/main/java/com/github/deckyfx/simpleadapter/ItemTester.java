package com.github.deckyfx.simpleadapter;

import android.support.annotation.Nullable;

import java.util.Comparator;

/**
 * Created by 1412 on 10/14/2017.
 */

public abstract class ItemTester {
    public static interface Compare<T extends BaseItem> extends Comparator<T> {

    }

    public abstract class Test<T extends BaseItem, V> {
        public abstract V apply(T item, @Nullable Object... params);
    }
}
