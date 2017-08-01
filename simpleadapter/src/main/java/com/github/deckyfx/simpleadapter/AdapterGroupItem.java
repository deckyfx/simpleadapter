package com.github.deckyfx.simpleadapter;

/**
 * Created by decky on 5/21/15.
 */
public class AdapterGroupItem<T extends BaseItem> extends AdapterItem {
    public AdapterDataSet<T> childrens;

    public AdapterGroupItem() {
        this(new AdapterDataSet<T>());
    }

    public AdapterGroupItem(AdapterDataSet<T> dataset) {
        super();
        this.childrens = dataset;
    }
}