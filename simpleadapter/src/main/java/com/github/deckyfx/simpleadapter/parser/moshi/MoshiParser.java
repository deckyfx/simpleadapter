package com.github.deckyfx.simpleadapter.parser.moshi;

import com.github.deckyfx.simpleadapter.BaseItem;
import com.github.deckyfx.simpleadapter.JSONParserAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

/**
 * Created by decky on 10/5/17.
 */

public class MoshiParser extends JSONParserAdapter {
    private Moshi MOSHI;

    public MoshiParser() {
    }

    @Override
    public void init() {
        this.MOSHI = new Moshi.Builder().build();
    }

    public Moshi getMoshi() {
        return this.MOSHI;
    }

    @Override
    public Object fromJson(String json, Class<? extends BaseItem> klas) throws IOException {
        return this.MOSHI.adapter(klas).fromJson(json);
    }

    @Override
    public Object fromJsonArray(String json, Class<? extends BaseItem[]> klas) throws IOException {
        return this.MOSHI.adapter(klas).fromJson(json);
    }

    @Override
    public String toJson(BaseItem baseItem) {
        return this.MOSHI.adapter(BaseItem.class).toJson(baseItem);
    }
}
