package com.github.deckyfx.simpleadapter;

/**
 * Created by decky on 8/1/17.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

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

    public void setGSON(Gson gson) {
        GSON = gson;
    }

    public Gson getGson() {
        return GSON;
    }

    public final <T extends BaseItem> T fromJson(String json) throws JsonSyntaxException {
        return (T) GSON.fromJson(json, this.getClass());
    }

    public final String toJson() throws JsonSyntaxException  {
        return GSON.toJson(this);
    }

    public final static <T extends BaseItem> T fromJson(String json, Class<? extends BaseItem> klas) throws JsonSyntaxException {
        return (T) GSON.fromJson(json, klas);
    }

    public final static String toJson(Object source) throws JsonSyntaxException {
        return GSON.toJson(source);
    }

    public final static String dateToString(String format, Date date) {
        return dateToString(format, date, Locale.getDefault());
    }

    public final static Date stringToDate(String format, String dateString) throws ParseException {
        return stringToDate(format, dateString, Locale.getDefault());
    }

    public final static String dateToString(String format, Date date, Locale locale) {
        DateFormat df = new SimpleDateFormat(format, locale);
        return df.format(date);
    }

    public final static Date stringToDate(String format, String dateString, Locale locale) throws ParseException {
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
