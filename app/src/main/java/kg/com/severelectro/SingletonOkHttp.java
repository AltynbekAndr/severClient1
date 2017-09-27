package kg.com.severelectro;

import okhttp3.OkHttpClient;

public class SingletonOkHttp {
    static OkHttpClient client = new OkHttpClient();

    public static OkHttpClient getClient() {
        return client;
    }


}