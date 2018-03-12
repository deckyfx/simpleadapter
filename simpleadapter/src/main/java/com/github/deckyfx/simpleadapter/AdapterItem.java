package com.github.deckyfx.simpleadapter;

public class AdapterItem extends BaseItem {
    public String text                              = "";
    public String text2                             = "";
    public Object data;

    public AdapterItem() {
        this("", "", null);
    }

    public AdapterItem(String text) {
        this(text, "", null);
    }

    public AdapterItem(String text, Object data) {
        this(text, "", data);
    }

    public AdapterItem(String text, String text2) {
        this(text, text2, null);
    }

    public AdapterItem(String text, String text2, Object data) {
        this.text   = text;
        this.text2  = text2;
        this.data   = data;
    }

    // Special method for test filter
    public boolean testFilter(CharSequence term) {
        return this.text.toLowerCase().contains(term.toString().toLowerCase()) || this.text2.toLowerCase().contains(term.toString().toLowerCase());
    }
}
