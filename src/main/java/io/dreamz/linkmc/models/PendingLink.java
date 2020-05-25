package io.dreamz.linkmc.models;

import java.time.Instant;
import java.util.UUID;

public final class PendingLink {

    private UUID id;
    private String service;
    private Instant expire;

    public boolean isExpired() {
        return expire.isAfter(Instant.now());
    }
}
