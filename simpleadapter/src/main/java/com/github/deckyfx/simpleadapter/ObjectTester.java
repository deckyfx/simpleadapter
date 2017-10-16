package com.github.deckyfx.simpleadapter;

import java.util.Comparator;

/**
 * Created by 1412 on 10/14/2017.
 */

public interface ObjectTester<T extends BaseItem> extends Comparator<T> {
    public boolean filter(T o, Object filter);
    public int getType(T o, int position);
}
