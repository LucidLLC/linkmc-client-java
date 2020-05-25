package io.dreamz.linkmc.models;

import java.util.Set;
import java.util.UUID;

public final class User {
    private UUID id;
    private Set<Link> links;
    private Set<PendingLink> pendingLinks;
}
