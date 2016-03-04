package me.chunsheng.utils;

/**
 * Created by weichunsheng on 16/3/3.
 */


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class OKHttpUtils<T> {
    public static final String GET = "GET";
    public static final String POST = "POST";
    private boolean DEBUG = true;
    private OkHttpClient client = null;
    private Context context;

    public OkHttpClient getClient() {
        return client;
    }

    private OKHttpUtils() {
    }

    private OKHttpUtils(Context context, int maxCacheSize, File cachedDir, final int maxCacheAge, List<Interceptor> netWorkinterceptors, List<Interceptor> interceptors, long timeOut, boolean debug) {
        client = new OkHttpClient();
        this.DEBUG = debug;
        this.context = context;
        if (cachedDir != null) {
            client.setCache(new Cache(cachedDir, maxCacheSize));
        } else {
            client.setCache(new Cache(context.getCacheDir(), maxCacheSize));
        }
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", String.format("max-age=%d", maxCacheAge))
                        .build();
            }
        };
        client.networkInterceptors().add(cacheInterceptor);
        if (netWorkinterceptors != null && !netWorkinterceptors.isEmpty()) {
            client.networkInterceptors().addAll(netWorkinterceptors);
        }
        if (interceptors != null && !interceptors.isEmpty()) {
            client.interceptors().addAll(interceptors);
        }
        client.setConnectTimeout(timeOut, TimeUnit.MILLISECONDS);
    }

    public OKHttpUtils initDefault(Context context) {
        return new Builder(context).build();
    }


    public void get(final String url, Callback callback) {
        request(url, GET, null, null, null, callback);
    }


    public RequestBody createRequestBody(Map<String, String> params, String encodedKey, String encodedValue) {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        if (params != null && !params.isEmpty()) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                formEncodingBuilder.add(key, params.get(key));
            }
        }
        if (!TextUtils.isEmpty(encodedKey) && !TextUtils.isEmpty(encodedValue)) {
            formEncodingBuilder.addEncoded(encodedKey, encodedValue);
        }
        return formEncodingBuilder.build();
    }

    public RequestBody createRequestBody(Map<String, String> params) {
        MultipartBuilder multipartBuilder = new MultipartBuilder();
        multipartBuilder.type(MultipartBuilder.FORM);
        if (params != null && !params.isEmpty()) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                multipartBuilder.addFormDataPart(key, params.get(key));
            }
        }
        return multipartBuilder.build();
    }


    public void request(final String url, final String method, final RequestBody requestBody, final Headers headers, final Object tag, final Callback callback) {
        requestFromNetwork(url, method, requestBody, headers, tag, callback);
    }

    public void requestFromNetwork(final String url, String method, RequestBody requestBody, Headers headers, Object tag, final Callback callback) {
        request(url, method, requestBody, CacheControl.FORCE_NETWORK, headers, tag, callback);
    }

    public void requestFromCached(String url, String method, RequestBody requestBody, Headers headers, Object tag, final Callback callback) {
        request(url, method, requestBody, CacheControl.FORCE_CACHE, headers, tag, callback);
    }


    private void request(String url, String method, RequestBody requestBody, final CacheControl cacheControl, Headers headers, Object tag, final Callback callback) {
        final Request.Builder requestBuilder = new Request.Builder().url(url).cacheControl(cacheControl);
        if (headers != null) {
            requestBuilder.headers(headers);
        }
        requestBuilder.method(method, requestBody);
        requestBuilder.tag(tag == null ? url : tag);

        final Request request = requestBuilder.build();
        request(request, new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                if (callback != null) {
                    callback.onFailure(request, e);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 504) {
                    if (CacheControl.FORCE_CACHE == cacheControl) {
                        if (callback != null) {
                            callback.onFailure(request, new IOException("cached not found"));
                        }
                        return;
                    }
                }
                if (callback != null) {
                    callback.onResponse(response);
                }
            }
        });
    }


    private Call request(Request request, Callback callback) {
        if (DEBUG) {
            Log.d("OKHttp", request.toString());
        }
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    public static class Builder {
        private int maxCachedSize = 5 * 1024 * 1024;
        private File cachedDir;
        private Context context;
        private List<Interceptor> networkInterceptors;
        private List<Interceptor> interceptors;
        private int maxCacheAge = 3600 * 12;
        private boolean isGzip = false;
        private long timeOut = 20000;
        private boolean debug = false;


        public Builder(Context context) {
            this.context = context;
        }

        private Builder() {
        }

        public OKHttpUtils build() {
            return new OKHttpUtils(context, maxCachedSize, cachedDir, maxCacheAge, networkInterceptors, interceptors, timeOut, debug);
        }

        public Builder timeOut(long timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder gzip(boolean openGzip) {
            this.isGzip = openGzip;
            return this;
        }

        public Builder cachedDir(File cachedDir) {
            this.cachedDir = cachedDir;
            return this;
        }

        public Builder interceptors(List<Interceptor> interceptors) {
            this.interceptors = interceptors;
            return this;
        }

        public Builder maxCachedSize(int maxCachedSize) {
            this.maxCachedSize = maxCachedSize;
            return this;
        }

        public Builder networkInterceptors(List<Interceptor> networkInterceptors) {
            this.networkInterceptors = networkInterceptors;
            return this;
        }

        public Builder maxCacheAge(int maxCacheAge) {
            this.maxCacheAge = maxCacheAge;
            return this;
        }
    }


    public void clearCached() {
        try {
            client.getCache().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

