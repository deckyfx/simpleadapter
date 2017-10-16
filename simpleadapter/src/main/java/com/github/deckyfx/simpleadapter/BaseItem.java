package com.github.deckyfx.simpleadapter;

/**
 * Created by decky on 8/1/17.
 */
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseItem {
    private static JSONParserAdapter Parser;

    public static void setJSONParser(JSONParserAdapter parser) {
        Parser = parser;
        Parser.init();
    }

    public static JSONParserAdapter getJSONParser() {
        return Parser;
    }

    public final <T extends BaseItem> T fromJson(String json) throws Exception {
        return (T) Parser.fromJson(json, this.getClass());
    }

    public final static <T extends BaseItem> T fromJson(String json, Class<? extends BaseItem> klas) throws Exception {
        return (T) Parser.fromJson(json, klas);
    }

    public final static <T extends BaseItem> T[] fromJsonArray(String json, Class<? extends BaseItem[]> klas) throws Exception {
        return (T[]) Parser.fromJsonArray(json, klas);
    }

    public final static <T extends BaseItem> T fromJson(String json, BaseItem obj) throws Exception {
        return (T) Parser.fromJson(json, obj.getClass());
    }

    public final static <T extends BaseItem> T[] fromJsonArray(String json, BaseItem... obj) throws Exception {
        return (T[]) Parser.fromJsonArray(json, obj.getClass());
    }

    public final String toJson() throws Exception {
        return Parser.toJson(this);
    }

    public final static String toJson(BaseItem source) throws Exception {
        return Parser.toJson(source);
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

    public boolean filter(ObjectTester<BaseItem> tester) {
        return true;
    }
}
