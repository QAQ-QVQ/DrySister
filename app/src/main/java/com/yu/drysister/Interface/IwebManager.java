package com.yu.drysister.Interface;

/**
 * CREATED BY DY ON 2019/7/15.
 * TIME BY 10:16.
 **/
public  interface IwebManager {
     void get(String url, IwebCallback iwebCallback);
     void get(String url,String cacheName, IwebCallback iwebCallback);
     void post(String url, IwebCallback iwebCallback);
     void put(String url,String requestBody, IwebCallback iwebCallback);
     void delete(String url,String requestBody, IwebCallback iwebCallback);
}
