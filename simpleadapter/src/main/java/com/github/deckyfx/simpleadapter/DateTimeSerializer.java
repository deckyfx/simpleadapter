package com.github.deckyfx.simpleadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by decky on 8/1/17.
 */

public class DateTimeSerializer implements JsonDeserializer<Date> {
    private static final String[] DATE_FORMATS = new String[] {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "timestamp"
    };

    @Override
    public Date deserialize(JsonElement jsonElement, Type typeOF, JsonDeserializationContext context) throws JsonParseException {
        for (String format : DATE_FORMATS) {
            if (format.equalsIgnoreCase("timestamp")) {
                try {
                    new Date(jsonElement.getAsLong());
                } catch (Exception   e) {
                }
            } else {
                try {
                    return new SimpleDateFormat(format, Locale.US).parse(jsonElement.getAsString());
                } catch (ParseException e) {
                }
            }
        }
        throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
                + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
    }
}
