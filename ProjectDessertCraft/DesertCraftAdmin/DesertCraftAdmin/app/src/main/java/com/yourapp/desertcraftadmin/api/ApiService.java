package com.yourapp.desertcraftadmin.api;


import com.yourapp.desertcraftadmin.product.ApiSingleResponse;
import com.yourapp.desertcraftadmin.productlist.ApiResponse;
import com.yourapp.desertcraftadmin.response.CallResponse;
import com.yourapp.desertcraftadmin.response.CatResponse;
import com.yourapp.desertcraftadmin.response.CountResponse;
import com.yourapp.desertcraftadmin.response.InsightResponse;
import com.yourapp.desertcraftadmin.response.OrderResponse;
import com.yourapp.desertcraftadmin.response.RateResponse;
import com.yourapp.desertcraftadmin.response.ShopResponse;
import com.yourapp.desertcraftadmin.response.TrackResponse;

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
    @POST("api/request?create_category")
    Call<CallResponse> createCategory(
            @Field("category") String category
    );

    @GET("api/request?get_category")
    Call<CatResponse> getCategory();

    @Multipart
    @Headers(CONTENT)
    @POST("api/request?upload_product")
    Call<CallResponse> uploadProduct(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("category") RequestBody category,
            @Part("variation") RequestBody variation,
            @Part("price") RequestBody price,
            @Part("base_price") RequestBody baseP,
            @Part("offer_price") RequestBody offerP,
            @Part MultipartBody.Part image
    );

    @Multipart
    @Headers(CONTENT)
    @POST("api/request?update_product")
    Call<CallResponse> updateProductWithFile(
            @Part("pid") RequestBody pid,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("category") RequestBody category,
            @Part("variation") RequestBody variation,
            @Part("price") RequestBody price,
            @Part("base_price") RequestBody baseP,
            @Part("offer_price") RequestBody offerP,
            @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?update_product")
    Call<CallResponse> updateProductWithoutFile(
            @Field("pid") String pid,
            @Field("name") String name,
            @Field("description") String description,
            @Field("category") String category,
            @Field("variation") String variation,
            @Field("price") String price,
            @Field("base_price") String baseP,
            @Field("offer_price") String offerP
    );

    @Headers(CONTENT)
    @GET("api/request?get_product_list")
    Call<ApiResponse> getProductList();

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?get_product")
    Call<ApiSingleResponse> getProduct(
            @Field("pid") String pid
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?delete_product")
    Call<CallResponse> deleteProduct(
            @Field("pid") String pid
    );


    //--  TO BE CORRECTED FROM HERE  --//
    @Headers(CONTENT)
    @GET("api/request?get_all_orders")
    Call<OrderResponse> getOrderList();

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?get_orders")
    Call<OrderResponse> getOrders(
            @Field("status") String status
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?track_order")
    Call<TrackResponse> trackOrder(
            @Field("item_id") String itemId
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

    @Headers(CONTENT)
    @GET("api/request?get_counts")
    Call<CountResponse> getStatusCount();

    //--  TO BE CORRECTED UP TO HERE  --//


    @GET("api/request?get_shop_location")
    Call<ShopResponse> getShopLocation();

    @Multipart
    @Headers(CONTENT)
    @POST("api/request?upload_shop")
    Call<CallResponse> uploadShopWithFile(
            @Part("name") RequestBody name,
            @Part("location") RequestBody location,
            @Part MultipartBody.Part image
    );

    @Multipart
    @Headers(CONTENT)
    @POST("api/request?update_shop")
    Call<CallResponse> updateShopWithFile(
            @Part("id") RequestBody id,
            @Part("name") RequestBody name,
            @Part("location") RequestBody location,
            @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?update_shop")
    Call<CallResponse> updateShopWithoutFile(
            @Field("id") String id,
            @Field("name") String name,
            @Field("location") String location
    );

    @Multipart
    @Headers(CONTENT)
    @POST("api/request?update_banner")
    Call<CallResponse> updateBannerWithFile(
            @Part MultipartBody.Part image
    );

    @Headers(CONTENT)
    @GET("api/request?get_ratings")
    Call<RateResponse> getRatings();

    @FormUrlEncoded
    @Headers(CONTENT)
    @POST("api/request?get_insights")
    Call<InsightResponse> getInsights(
            @Field("startDate") String startDate,
            @Field("endDate") String endDate
    );

}

