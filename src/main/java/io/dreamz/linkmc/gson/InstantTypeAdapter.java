package io.dreamz.linkmc.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

public final class InstantTypeAdapter implements JsonDeserializer<Instant>, JsonSerializer<Instant> {
    @Override
    public Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonPrimitive()) {
            return Instant.ofEpochMilli(jsonElement.getAsLong());
        }
        return null;
    }

    @Override
    public JsonElement serialize(Instant instant, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(instant.toEpochMilli());
    }
}
