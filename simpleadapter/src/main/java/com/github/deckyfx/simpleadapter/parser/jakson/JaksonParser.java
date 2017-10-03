package com.github.deckyfx.simpleadapter.parser.jakson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deckyfx.simpleadapter.BaseItem;
import com.github.deckyfx.simpleadapter.JSONParserAdapter;
import com.google.gson.Gson;

import java.io.IOException;

public class JaksonParser extends JSONParserAdapter {
    private ObjectMapper mObjectMapper;

    public JaksonParser() {
    }

    @Override
    public void init() {
        this.mObjectMapper = new ObjectMapper();
    }

    public ObjectMapper getmObjectMapper() {
        return this.mObjectMapper;
    }

    @Override
    public Object fromJson(String json, Class<? extends BaseItem> klas) throws IOException {
        return this.mObjectMapper
                .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .readValue(json, klas);
    }

    @Override
    public Object fromJsonArray(String json, Class<? extends BaseItem[]> klas) throws IOException {
        return this.mObjectMapper
                .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .readValue(json, klas);
    }

    @Override
    public String toJson(BaseItem baseItem) {
        String result = "";
        try {
            result = this.mObjectMapper.writeValueAsString(baseItem);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}