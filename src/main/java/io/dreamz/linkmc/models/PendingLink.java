package io.dreamz.linkmc.models;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class PendingLink {

    private UUID id;
    private String service;
    private Instant expire;

    public boolean isExpired() {
        return expire.isAfter(Instant.now());
    }


    public UUID id() {
        return id;
    }

    public String service() {
        return service;
    }

    @Override
    public String toString() {
        return "PendingLink{" +
                "id=" + id +
                ", service='" + service + '\'' +
                ", expire=" + expire +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingLink that = (PendingLink) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(service, that.service) &&
                Objects.equals(expire, that.expire);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, service, expire);
    }
}
