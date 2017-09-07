import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class GSONParser extends JSONParserAdapter {
    private Gson GSON;

    public GSONParser() {}

    @Override
    public void init() {
        this.GSON = new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy MM DD HH:mm:ss ")
                .registerTypeAdapter(Date.class, new DateTimeSerializer())
                .create();
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