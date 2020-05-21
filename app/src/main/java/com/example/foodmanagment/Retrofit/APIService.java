package com.example.foodmanagment.Retrofit;


import com.example.foodmanagment.Models.Registeration.SignUp;
import com.example.foodmanagment.Models.checkUser.VerifyUser;
import com.example.foodmanagment.Models.getCatogories.Categories;
import com.example.foodmanagment.Models.getCompanies.Companies;
import com.example.foodmanagment.Models.getCompleteOrder.CompleteOrder;
import com.example.foodmanagment.Models.getOrderDetail.OrderDetail;
import com.example.foodmanagment.Models.getProducts.Prods;
import com.example.foodmanagment.Models.getProfile.UserProfile;
import com.example.foodmanagment.Models.getUserOrders.UserOrders;
import com.example.foodmanagment.Models.login.LoginModel;
import com.example.foodmanagment.Models.updatePassword.UpdatePassword;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIService {



    @POST
    Call<LoginModel> login(@Url String url, @Body JsonObject jsonObject);

    @POST
    Call<Companies> getCompanies(@Url String url, @Body JsonObject jsonObject);

    @POST
    Call<Categories> getCategories(@Url String url, @Body JsonObject jsonObject);

    @POST
    Call<Prods> getProducts(@Url String url, @Body JsonObject jsonObject);

    @POST
    Call<SignUp> signUp(@Url String url, @Body JsonObject jsonObject);

    @POST
    Call<SignUp> sendOrder(@Url String url, @Body JsonObject jsonObject);

    @POST
    Call<UserOrders> getUserOrders(@Url String url, @Body JsonObject jsonObject);

    @POST
    Call<OrderDetail> getOrderDetail(@Url String url, @Body JsonObject jsonObject);

    @POST
    Call<CompleteOrder> completeOrder(@Url String url, @Body JsonObject jsonObject);

    @POST
    Call<UserProfile> getProfile(@Url String url, @Body JsonObject jsonObject);

    @POST
    Call<VerifyUser> verifyUser(@Url String url, @Body JsonObject jsonObject);


    @POST
    Call<UpdatePassword> updatePassword(@Url String url, @Body JsonObject jsonObject);

}