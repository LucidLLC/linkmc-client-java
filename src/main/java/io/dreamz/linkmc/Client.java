package io.dreamz.linkmc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.dreamz.linkmc.gson.InstantTypeAdapter;
import io.dreamz.linkmc.gson.UUIDTypeAdapter;
import io.dreamz.linkmc.models.APIException;
import io.dreamz.linkmc.models.User;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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


    public static Gson createDefaultGson() {
        return new GsonBuilder()
                .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter()).create();
    }

    private Gson gson;
    private String apiBaseUrl;
    private OkHttpClient okHttpClient;

    public Client(Gson gson, String apiBaseUrl, String key) {
        this.gson = gson;
        this.apiBaseUrl = apiBaseUrl;
        this.okHttpClient = new OkHttpClient.Builder().addInterceptor(new AuthInterceptor(key)).callTimeout(5, TimeUnit.SECONDS).build();
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
