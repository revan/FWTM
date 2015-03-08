package com.rsopher.fwtm;


import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by revan on 3/8/15.
 */
public interface ServerClient {
    @POST("/update/loc/{lat}/{lon}/{id}")
    Response updateLocation(@Path("lat") String lat, @Path("lon") String lon, @Path("id") String id);

    @POST("/update/attack/{id_me}/{id_them}")
    Response sendAttack(@Path("id_me") String id_me, @Path("id_them") String id_them);

    @GET("/update/status")
    ServerStatus getStatus();
}
