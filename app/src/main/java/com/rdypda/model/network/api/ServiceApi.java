package com.rdypda.model.network.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by DengJf on 2017/12/8.
 */

public interface ServiceApi {

    @GET("QuerySqlCommand")
    Call<String>getQuerysqlResult(@Query("SqlCommand")String sql,@Query("Password")String password);

}
