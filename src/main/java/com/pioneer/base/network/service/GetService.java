package com.pioneer.base.network.service;

import com.pioneer.base.network.model.BaseModel;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface GetService<T extends BaseModel> {
    @GET("{path}")
    Observable<T> executeTask(@Path("path") String path,@HeaderMap Map<String,String> commonHeader,@QueryMap Map<String,String> commonFields);
}
