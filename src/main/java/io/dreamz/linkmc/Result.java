package io.dreamz.linkmc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public final class Result {

    private final int statusCode;
    private final byte[] body;

    public Result(int statusCode, byte[] body) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getBody() {
        return body;
    }

    public boolean hasBody() {
        return this.body.length > 0;
    }

    public JsonElement toJson(Gson gson) {
        return gson.fromJson(new InputStreamReader(new ByteArrayInputStream(this.body)), JsonElement.class);
    }

    public <T> T parse(Gson gson, Class<? extends T> clazz) {
        return gson.fromJson(new InputStreamReader(new ByteArrayInputStream(this.body)), clazz);
    }
}
