package com.github.deckyfx.simpleadapter;

import java.io.IOException;

/**
 * Created by decky on 9/7/17.
 */

public abstract class JSONParserAdapter {
    public abstract void init();

    public abstract Object fromJson(String json, Class<? extends BaseItem> klas) throws Exception;

    public abstract Object fromJsonArray(String json, Class<? extends BaseItem[]> klas) throws IOException;

    public abstract String toJson(BaseItem baseItem);
}
