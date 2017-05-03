package com.github.deckyfx.simpleadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by decky on 5/6/15.
 */
public class AdapterItem {
    /*
    * JakSon Anotation Usage
    *
    // means that if we see "foo" or "bar" in JSON, they will be quietly skipped
    // regardless of whether POJO has such properties
    @JsonIgnoreProperties({ "foo", "bar" })
    // Finally, you may even want to just ignore any "extra" properties from JSON
    // (ones for which there is no counterpart in POJO). This can be done by adding:
    //@JsonIgnoreProperties(ignoreUnknown=true)

    // will not be written as JSON; nor assigned from JSON:
    @JsonIgnore
    public String _internal;

    // although nominal type is 'String', we want to read JSON as 'Integer'
    @JsonDeserialize(as=Integer.class)
    public String _value;

    // although runtime type may be 'String', we really want to serialize
    // as 'int'; two ways to do this:
    @JsonSerialize(as=String.class)
    // or could also use: @JsonSerialize(typing=Typing.STATIC)
    public int _another;

    // handle date
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="UTC")
    public Date _date;

    // rename property
    @JsonProperty("renametoother")
    public String renamed;

    // Assign only, so only can created by JSON, but when creating json _name is skiped
    private String _name;
    @JsonProperty
    public void setName(String n) { _name = n; }
    @JsonIgnore
    public String getName() { return _name; }
    */

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="UTC")
    public Date _date;

    public String text                              = "";
    public String text2                             = "";
    public Object data;

    @JsonIgnore
    protected final ObjectMapper mObjectMapper      = new ObjectMapper();

    @JsonIgnore
    public AdapterItem() {
        this("", "", null);
    }

    @JsonIgnore
    public AdapterItem(String text) {
        this(text, "", null);
    }

    @JsonIgnore
    public AdapterItem(String text, Object data) {
        this(text, "", data);
    }

    @JsonIgnore
    public AdapterItem(String text, String text2) {
        this(text, text2, null);
    }

    @JsonIgnore
    public AdapterItem(String text, String text2, Object data) {
        this.text = text;
        this.text2 = text2;
        this.data = data;
    }

    @JsonIgnore
    public final <T extends AdapterItem> T parseJackson(String text) throws IOException {
        return (T) this.mObjectMapper.readValue(text, this.getClass());
    }

    @JsonIgnore
    public final <T extends AdapterItem> T mergeJackson(String text) throws IOException {
        ObjectReader updater = this.mObjectMapper.readerForUpdating(this.getClass());
        return (T) updater.readValue(text);
    }

    @JsonIgnore
    public final String toString() {
        String result = "";
        try {
            result = this.mObjectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @JsonIgnore
    public final static <T extends AdapterItem> T ParseJackson(String text, Class<? extends AdapterItem> klas) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return (T) objectMapper.readValue(text, klas);
    }

    @JsonIgnore
    public final static <T extends AdapterItem> T MergeJackson(String text, Class<? extends AdapterItem> klas) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader updater = objectMapper.readerForUpdating(klas);
        return (T) updater.readValue(text);
    }

    public final static ObjectReader getObjectReader(Class<?> klas){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader reader = objectMapper.readerFor(klas);
        return reader;
    }

    @JsonIgnore
    public final static String ToString(Object obj) {
        String result = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            result = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @JsonIgnore
    public final static String parseDateAsString(String format, Date date) {
        return parseDateAsString(format, date, Locale.getDefault());
    }

    @JsonIgnore
    public final static Date parseStringAsDate(String format, String dateString) {
        return parseStringAsDate(format, dateString, Locale.getDefault());
    }

    @JsonIgnore
    public final static String parseDateAsString(String format, Date date, Locale locale) {
        DateFormat df = new SimpleDateFormat(format, locale);
        return df.format(date);
    }

    @JsonIgnore
    public final static Date parseStringAsDate(String format, String dateString, Locale locale) {
        DateFormat df = new SimpleDateFormat(format, locale);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    // Special method for test filter
    @JsonIgnore
    public boolean testFilter(CharSequence term) {
        return this.text.toLowerCase().contains(term.toString().toLowerCase()) || this.text2.toLowerCase().contains(term.toString().toLowerCase());
    }

    public static class ViewHolder {
        protected View mConvertView;
        protected TextView mTextView1, mTextView2;
        protected CheckedTextView mCheckedTextView1, mCheckedTextView2;
        protected ClickListener mClickListener;
        protected TouchListener mTouchListener;
        protected int mLayoutTag;

        public ViewHolder() {

        }

        public ViewHolder(View convertView) {
            this.mConvertView = convertView;
            View view = convertView.findViewById(android.R.id.text1);
            if (view instanceof CheckedTextView) {
                this.mCheckedTextView1 = (CheckedTextView) view;
            } else if (view instanceof TextView) {
                this.mTextView1 = (TextView) view;
            }
            view = convertView.findViewById(android.R.id.text2);
            if (view instanceof CheckedTextView) {
                this.mCheckedTextView2 = (CheckedTextView) view;
            } else if (view instanceof TextView) {
                this.mTextView2 = (TextView) view;
            }
        }

        public void setupView(Context ctx, int position, AdapterItem itemobject) {
            this.setupView(ctx, position, 0, itemobject);
        }

        public void setupView(Context ctx, int groupPosition, int childPosition, AdapterItem itemobject) {
            if (itemobject != null) {
                if (this.mTextView1 != null) {
                    this.mTextView1.setText(itemobject.text);
                    this.mTextView1.setTag(groupPosition);
                }
                if (this.mTextView2 != null) {
                    this.mTextView2.setText(itemobject.text2);
                    this.mTextView2.setTag(groupPosition);
                }
                if (this.mCheckedTextView1 != null) {
                    this.mCheckedTextView1.setText(itemobject.text);
                    this.mCheckedTextView1.setTag(groupPosition);
                }
                if (this.mCheckedTextView2 != null) {
                    this.mCheckedTextView2.setText(itemobject.text2);
                    this.mCheckedTextView2.setTag(groupPosition);
                }
            }
        }

        public void setClickListener(ClickListener clickListener) {
            this.mClickListener = clickListener;
        }

        public void setTouchListener(TouchListener touchListener) {
            this.mTouchListener = touchListener;
        }

        public interface ClickListener extends View.OnClickListener {
            @Override
            public void onClick(View view);
        }

        public interface TouchListener extends View.OnTouchListener {
            @Override
            public boolean onTouch(View view, MotionEvent mv);
        }
    }

    public static class RecycleViewHolder extends RecyclerView.ViewHolder {
        protected View mConvertView;
        protected TextView mTextView1, mTextView2;
        protected CheckedTextView mCheckedTextView1, mCheckedTextView2;
        protected ClickListener mClickListener;
        protected TouchListener mTouchListener;
        protected int mLayoutTag;

        public RecycleViewHolder() {
            super(null);
        }

        public RecycleViewHolder(View convertView) {
            super(null);
            this.mConvertView = convertView;
            this.mTextView1 = (TextView) convertView.findViewById(android.R.id.text1);
            this.mTextView2 = (TextView) convertView.findViewById(android.R.id.text2);
            this.mCheckedTextView1 = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            this.mCheckedTextView2 = (CheckedTextView) convertView.findViewById(android.R.id.text2);
        }

        public void setupView(Context ctx, int position, AdapterItem itemobject) {
            this.setupView(ctx, position, 0, itemobject);
        }

        public void setupView(Context ctx, int groupPosition, int childPosition, AdapterItem itemobject) {
            if (itemobject != null) {
                if (this.mTextView1 != null) {
                    this.mTextView1.setText(itemobject.text);
                    this.mTextView1.setTag(groupPosition);
                }
                if (this.mTextView2 != null) {
                    this.mTextView2.setText(itemobject.text2);
                    this.mTextView2.setTag(groupPosition);
                }
                if (this.mCheckedTextView1 != null) {
                    this.mCheckedTextView1.setText(itemobject.text);
                    this.mCheckedTextView1.setTag(groupPosition);
                }
                if (this.mCheckedTextView2 != null) {
                    this.mCheckedTextView2.setText(itemobject.text2);
                    this.mCheckedTextView2.setTag(groupPosition);
                }
            }
        }

        public void setClickListener(ClickListener clickListener) {
            this.mClickListener = clickListener;
        }

        public void setTouchListener(TouchListener touchListener) {
            this.mTouchListener = touchListener;
        }

        public void setLayoutTag(int layoutTag) {
            this.mConvertView.setTag(layoutTag);
            this.mLayoutTag = layoutTag;
        }

        public interface ClickListener extends View.OnClickListener {
            @Override
            public void onClick(View view);
        }

        public interface TouchListener extends View.OnTouchListener {
            @Override
            public boolean onTouch(View view, MotionEvent mv);
        }
    }
}
