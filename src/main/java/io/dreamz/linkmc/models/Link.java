package io.dreamz.linkmc.models;

import java.time.Instant;
import java.util.Objects;

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


    @Override
    public String toString() {
        return "Link{" +
                "service='" + service + '\'' +
                ", username='" + username + '\'' +
                ", addedAt=" + addedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(service, link.service) &&
                Objects.equals(username, link.username) &&
                Objects.equals(addedAt, link.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, username, addedAt);
    }
}
