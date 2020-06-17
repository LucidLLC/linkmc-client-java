package io.dreamz.linkmc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.dreamz.linkmc.gson.InstantTypeAdapter;
import io.dreamz.linkmc.gson.UUIDTypeAdapter;
import io.dreamz.linkmc.models.APIException;
import io.dreamz.linkmc.models.Link;
import io.dreamz.linkmc.models.User;
import io.dreamz.linkmc.models.VerifyPayload;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Client {

    private static class AuthInterceptor implements Interceptor {
        private String key;

        AuthInterceptor(String key) {
            this.key = key;
        }

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request.Builder authenticatedRequest = chain.request().newBuilder();
            authenticatedRequest.addHeader("X-Link-Auth-Token", key);
            return chain.proceed(authenticatedRequest.build());
        }
    }


    private static class ClientWebSocketListener extends WebSocketListener {
        private Gson gson;
        private Set<Consumer<VerifyPayload>> listeners;

        public ClientWebSocketListener(Gson gson, Set<Consumer<VerifyPayload>> listeners) {
            this.gson = gson;
            this.listeners = listeners;
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            try {
                VerifyPayload verifyPayload = this.gson.fromJson(text, VerifyPayload.class);

                for (Consumer<VerifyPayload> listener : this.listeners) {
                    listener.accept(verifyPayload);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public static Gson createDefaultGson() {
        return new GsonBuilder()
                .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter()).create();
    }

    private Gson gson;
    private String apiBaseUrl;
    private OkHttpClient okHttpClient;

    private Set<Consumer<VerifyPayload>> listeners;

    public Client(Gson gson, URI apiBaseUrl, String key) {
        this.gson = gson;
        this.apiBaseUrl = apiBaseUrl.getScheme() + "://" + apiBaseUrl.getHost() + ":" + apiBaseUrl.getPort();
        this.okHttpClient = new OkHttpClient.Builder().addInterceptor(new AuthInterceptor(key)).callTimeout(5, TimeUnit.SECONDS)
                .build();


        this.listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

        this.okHttpClient.newWebSocket(new Request.Builder().url("ws://" + apiBaseUrl.getHost() + ":" + apiBaseUrl.getPort() + "/messages").build(), new ClientWebSocketListener(gson, this.listeners));
    }


    public Call prepareCall(String path) {
        return okHttpClient.newCall(new Request.Builder().url(apiBaseUrl + path).build());
    }

    public <T> CompletableFuture<T> get(String route, Class<? extends T> as) {
        CompletableFuture<T> future = new CompletableFuture<>();

        try {
            final Call preparedCall = this.prepareCall(route);

            preparedCall.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200) {
                        final APIException exception = getException(response.code(), response.body() == null ? "" : Objects.requireNonNull(response.body()).string());
                        future.completeExceptionally(exception);
                    } else {
                        future.complete(gson.fromJson(Objects.requireNonNull(response.body()).string(), as));
                    }
                }
            });


        } catch (Throwable e) {

            future.completeExceptionally(e);
        }

        return future;
    }

    public CompletableFuture<User> getUser(UUID playerId) {
        return this.get(String.format("/links/%s", playerId.toString()), User.class);
    }

    public CompletableFuture<String> startLink(String platform, UUID playerId, int expire) {
        return this.get(String.format("/startlink/%s/%s/%d", platform, playerId.toString(), expire), String.class);
    }


    /**
     * Registers a verification listener which gets called
     *
     * @param verifyListener
     * @return
     */
    public Client onVerify(Consumer<VerifyPayload> verifyListener) {
        this.listeners.add(verifyListener);
        return this;
    }


    public APIException getException(int code, String body) {
        switch (code) {
            case 400:
                return new APIException.BadRequestException(body);
            case 403:
                return new APIException.ForbiddenException(body);
            case 404:
                return new APIException.NotFoundException(body);
            default:
            case 500:
                return new APIException.InternalServerException(body);
        }
    }
}
