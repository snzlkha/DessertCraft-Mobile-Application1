package com.yourapp.desertcraft.api;

import com.yourapp.desertcraft.productlist.ApiResponse;
import com.yourapp.desertcraft.response.BannerResponse;
import com.yourapp.desertcraft.response.CallResponse;
import com.yourapp.desertcraft.response.CatResponse;
import com.yourapp.desertcraft.response.LoginResponse;
import com.yourapp.desertcraft.response.OrderResponse;
import com.yourapp.desertcraft.response.ProfileResponse;
import com.yourapp.desertcraft.response.RateResponse;
import com.yourapp.desertcraft.response.RewardResponse;
import com.yourapp.desertcraft.response.ShopResponse;
import com.yourapp.desertcraft.response.TrackResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    String CONTENT = "Accept: application/json";

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?get_login_data")
    Call<LoginResponse> getLoginData(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?set_signup_data")
    Call<CallResponse> registerUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("dob") String dob,
            @Field("gender") String gender,
            @Field("mobile") String mobile,
            @Field("password") String password
    );

    @GET("api/request?get_category")
    Call<CatResponse> getCategory();

    @GET("api/request?get_desert_list")
    Call<ApiResponse> getDesertList();

    @GET("api/request?get_shop_location")
    Call<ShopResponse> getShopLocation();

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?save_order")
    Call<CallResponse> saveOrder(
            @Field("uid") String uid,
            @Field("did") String did,
            @Field("image") String image,
            @Field("desert_name") String desert,
            @Field("description") String description,
            @Field("customization") String customization,
            @Field("price") String price,
            @Field("quantity") String quantity,
            @Field("date") String date,
            @Field("pointUsed") String pointUsed,
            @Field("instruction") String instruction
    );

    @Multipart
    @Headers(CONTENT)
    @POST("api/request?save_order")
    Call<CallResponse> saveOrderWithDesign(
            @Part("uid") RequestBody uid,
            @Part("did") RequestBody did,
            @Part("image") RequestBody image,
            @Part("desert_name") RequestBody desert,
            @Part("description") RequestBody description,
            @Part("customization") RequestBody customization,
            @Part("price") RequestBody price,
            @Part("quantity") RequestBody quantity,
            @Part("date") RequestBody date,
            @Part("pointUsed") RequestBody pointUsed,
            @Part("instruction") RequestBody instruction,
            @Part MultipartBody.Part design
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?get_order_list")
    Call<OrderResponse> getOrderList(
            @Field("uid") String uid
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?track_order")
    Call<TrackResponse> trackOrder(
            @Field("item_id") int itemId
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?forward_order")
    Call<CallResponse> forwardOrder(
            @Field("addonPrice") String addonPrice,
            @Field("order_status") String oStatus,
            @Field("tracking_status") String tStatus,
            @Field("item_id") String itemId,
            @Field("uid") String uid,
            @Field("newPoints") int newPoints
    );

    @Multipart
    @Headers(CONTENT)
    @POST("api/request?forward_order_with_receipt")
    Call<CallResponse> forwardOrderWithReceipt(
            @Part("order_status") RequestBody oStatus,
            @Part("tracking_status") RequestBody tStatus,
            @Part("item_id") RequestBody itemId,
            @Part("uid") RequestBody uid,
            @Part MultipartBody.Part receipt
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?withdraw_order")
    Call<CallResponse> withdrawOrder(
            @Field("uid") String uid,
            @Field("orderId") String oid
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?save_rating")
    Call<CallResponse> saveRating(
            @Field("order_id") int orderId,
            @Field("desert_id") int desertId,
            @Field("rating") float rating,
            @Field("comments") String comments
    );

    @GET("api/request?get_promo_banner")
    Call<BannerResponse> getPromoBanner();

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?get_points")
    Call<RewardResponse> getPoints(
            @Field("uid") String uid
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?get_profile")
    Call<ProfileResponse> getProfile(
            @Field("uid") String uid
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?get_product_ratings")
    Call<RateResponse> getRatings(
            @Field("pid") String pid
    );

    @Multipart
    @Headers(CONTENT)
    @POST("api/request?update_profile")
    Call<CallResponse> updateProfileWithImage(
            @Part("uid") RequestBody uid,
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("mobile") RequestBody mobile,
            @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?update_profile")
    Call<CallResponse> updateProfileWithoutImage(
            @Field("uid") String uid,
            @Field("name") String name,
            @Field("email") String email,
            @Field("mobile") String mobile
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?update_password")
    Call<CallResponse> updatePassword(
            @Field("uid") String uid,
            @Field("email") String email,
            @Field("currentPassword") String cuPass,
            @Field("newPassword") String newPass
    );

}

