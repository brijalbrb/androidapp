package s.com.videoapp.utils;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by arthtilva on 22-Dec-16.
 */

public interface API {
    String URLPrefix = "/mdriver/";

    @POST(URLPrefix + "login")
    Call<ResponseBody> login(
            @Header("username") String username,
            @Header("password") String password,
            @Body RequestBody userDevice
    );


    @POST(URLPrefix + "update_request_status")
    Call<ResponseBody> updateRequestStatus(
            @Header("accessToken") String accessToken,
            @Header("driverId") long driverId,
            @Body RequestBody request
    );


    @POST(URLPrefix + "update_driver_status")
    Call<ResponseBody> changeDriverStatus(
            @Header("accessToken") String accessToken,
            @Header("driverId") String driverId,
            @Body RequestBody driver
    );




    @POST(URLPrefix + "change_password")
    Call<ResponseBody> changePassword(
            @Header("accessToken") String accessToken,
            @Header("driverId") long driverId,
            @Header("newPassword") String newPassword,
            @Header("password") String password
    );



    @GET(URLPrefix + "current_request")
    Call<ResponseBody> current_request(
            @Header("accessToken") String accessToken,
            @Header("driverId") String driverId
    );

    @GET(URLPrefix + "get_profile")
    Call<ResponseBody> getProfile(
            @Header("accessToken") String accessToken,
            @Header("driverId") String driverId
    );


    @POST(URLPrefix + "past_request")
    Call<ResponseBody> past_request(
            @Header("accessToken") String accessToken,
            @Header("driverId") String driverId,
            @Body RequestBody paginate
    );

    @POST(URLPrefix + "update_driver_location")
    Call<ResponseBody> update_driver_location(
            @Header("accessToken") String accessToken,
            @Header("driverId") String driverId,
            @Body RequestBody paginate
    );

}