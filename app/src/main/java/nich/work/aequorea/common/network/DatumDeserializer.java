package nich.work.aequorea.common.network;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import nich.work.aequorea.common.utils.GsonUtils;
import nich.work.aequorea.model.entity.Datum;

public class DatumDeserializer implements JsonDeserializer<Datum> {
    
    @Override
    public Datum deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Datum datum = GsonUtils.gson.fromJson(json, Datum.class);
        JsonObject jsonObject = json.getAsJsonObject();
        
        if (jsonObject.has("data")) {
            JsonElement elem = jsonObject.get("data")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("cover_url");
            if (elem != null && !elem.isJsonNull()) {
                if (elem.isJsonPrimitive()) {
                    String valuesString = elem.getAsString();
                    datum.getData().get(0).setCoverUrl(valuesString);
                } else if (elem.isJsonArray()) {
                    datum.getData().get(0).setCoverUrl(elem.getAsJsonArray().get(0).getAsString());
                }
            }
        }
        return datum;
    }
}