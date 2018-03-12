package com.github.deckyfx.simpleadapter.parser.gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by decky on 8/1/17.
 */

public class DateTimeSerializer implements JsonDeserializer<Date>, JsonSerializer<Date> {
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
                    return new Date(jsonElement.getAsLong());
                } catch (Exception   e) {
                }
            } else {
                try {
                    return new SimpleDateFormat(format, Locale.US).parse(jsonElement.getAsString());
                } catch (ParseException e) {
                }
            }
        }
        throw new JsonParseException("Unable to parse date: \"" + jsonElement.getAsString() + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new GsonBuilder().create().toJsonTree(new SimpleDateFormat(DATE_FORMATS[0], Locale.US).format(src));
    }
}
