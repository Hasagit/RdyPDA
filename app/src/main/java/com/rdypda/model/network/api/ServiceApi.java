package com.rdypda.model.network.api;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by DengJf on 2017/12/8.
 */

public interface ServiceApi {

    @GET("QuerySqlCommand")
    Call<String>getQuerysqlResult(@Query("SqlCommand")String sql,@Query("Password")String password);

    @GET("GetCompanyList")
    Call<String>getCompanyList();

    @GET("UsrLogon")
    Call<String>userLogin(@Query("usrCmpId")String usrCmpId,@Query("usrId")String usrId,@Query("usrPwd")String usrPwd);

    @GET("QuerySqlCommandJosn")
    Call<String>querySqlCommandJosn(@Query("SqlCommand")String SqlCommand,@Query("cTokenUser")String cTokenUser);
}
