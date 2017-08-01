package com.github.deckyfx.simpleadapter;

/**
 * Created by decky on 8/1/17.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseItem {
    private static Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Date.class,new UnixTimestampDeserializer())
            .registerTypeAdapter(Date.class, new DateTimeSerializer())
            .create();

    private void setGSON(Gson gson) {
        GSON = gson;
    }

    protected final <T extends BaseItem> T fromJson(String json) {
        return (T) GSON.fromJson(json, this.getClass());
    }

    protected final String toJson() {
        return GSON.toJson(this);
    }

    public final static <T extends BaseItem> T fromJson(String json, Class<? extends BaseItem> klas){
        return (T) GSON.fromJson(json, klas);
    }

    public final static String toJson(Object source){
        return GSON.toJson(source);
    }

    public final static String dateToString(String format, Date date) {
        return dateToString(format, date, Locale.getDefault());
    }

    public final static Date stringToDate(String format, String dateString) {
        return stringToDate(format, dateString, Locale.getDefault());
    }

    public final static String dateToString(String format, Date date, Locale locale) {
        DateFormat df = new SimpleDateFormat(format, locale);
        return df.format(date);
    }

    public final static Date stringToDate(String format, String dateString, Locale locale) {
        DateFormat df = new SimpleDateFormat(format, locale);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public boolean testFilter(CharSequence constraint) {
        return true;
    }
}
