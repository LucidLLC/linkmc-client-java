package io.dreamz.linkmc.models;

import java.time.Instant;

public final class Link {

    private String service;
    private String username;
    private Instant addedAt;

    public String service() {
        return service;
    }

    public String username() {
        return username;
    }

    public Instant addedAt() {
        return addedAt;
    }
}
