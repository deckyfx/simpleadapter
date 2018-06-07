package com.github.deckyfx.simpleadapter.parser.gson;

import com.github.deckyfx.simpleadapter.BaseItem;
import com.github.deckyfx.simpleadapter.JSONParserAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class GSONParser extends JSONParserAdapter {
    private Gson GSON;

    public GSONParser() {
    }

    @Override
    public void init() {
        this.GSON = new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy MM DD HH:mm:ss")
                .registerTypeAdapter(Date.class, new DateTimeSerializer())
                .create();
    }

    public Gson getGSON() {
        return this.GSON;
    }

    @Override
    public Object fromJson(String json, Class<? extends BaseItem> klas) {
        return this.GSON.fromJson(json, klas);
    }

    @Override
    public Object fromJsonArray(String json, Class<? extends BaseItem[]> klas) {
        return this.GSON.fromJson(json, klas);
    }

    @Override
    public String toJson(BaseItem baseItem) {
        return this.GSON.toJson(baseItem);
    }
}