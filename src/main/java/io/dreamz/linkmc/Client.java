package io.dreamz.linkmc;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class Client {


    private static class BaseURLInterceptor implements Interceptor {

        private String baseUrl;

        public BaseURLInterceptor(String baseUrl) {
            this.baseUrl = baseUrl;
        }


        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request.Builder baseurlReq = chain.request().newBuilder();
            baseurlReq.url(baseUrl + chain.request().url().host());
            return chain.proceed(baseurlReq.build());
        }
    }

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

    private String apiBaseUrl;
    private OkHttpClient okHttpClient;

    public Client(String apiBaseUrl, String key) {
        this.apiBaseUrl = apiBaseUrl;
        this.okHttpClient = new OkHttpClient.Builder().addInterceptor(new AuthInterceptor(key)).callTimeout(5, TimeUnit.SECONDS).build();
    }


    public Response makeRequest(String path) throws IOException {
        return okHttpClient.newCall(new Request.Builder().url(apiBaseUrl + path).build()).execute();
    }
}
