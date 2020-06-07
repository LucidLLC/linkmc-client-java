package io.dreamz.linkmc.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public final class UUIDTypeAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID> {
    @Override
    public UUID deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        if (jsonElement.isJsonPrimitive()) {
            return UUID.fromString(jsonElement.getAsString());
        }
        return null;
    }

    @Override
    public JsonElement serialize(UUID uuid, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(uuid.toString());
    }
}
