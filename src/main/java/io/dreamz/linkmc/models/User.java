package io.dreamz.linkmc.models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class User {
    private UUID id;
    private Set<Link> links;
    @SerializedName("pending_links")
    private Set<PendingLink> pendingLinks;


    public Set<Link> links() {
        return links;
    }

    public Set<PendingLink> pendingLinks() {
        return pendingLinks;
    }

    public UUID id() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", links=" + links +
                ", pendingLinks=" + pendingLinks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(links, user.links) &&
                Objects.equals(pendingLinks, user.pendingLinks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, links, pendingLinks);
    }
}
