
 package com.example.docscanx.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofitclient = null;
    private static Retrofit retrofitclient1 = null;
    public static Retrofit getClient()
    {
        if(retrofitclient==null)
        {
            retrofitclient = new Retrofit.Builder()
                    .baseUrl("http://3.128.76.178")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        }
        return retrofitclient;
    }
    public static Retrofit getClient1()
    {
        if(retrofitclient1==null)
        {
            retrofitclient1 = new Retrofit.Builder()
                    .baseUrl("http://3.128.76.178")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        }
        return retrofitclient1;
    }
}
