package com.codechef.ffds

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

val gson: Gson = GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .create()

val retrofit: Retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
    .baseUrl("https://ffds-backend.azurewebsites.net/").build()

val retrofitForSlots: Retrofit =
    Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://54.91.224.22:8000/docs/").build()

interface ApiHolder {
    @FormUrlEncoded
    @POST("user/register")
    fun register(@FieldMap fields: Map<String, String>): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("user/login")
    fun login(@FieldMap fields: Map<String, String>): Call<Token?>?

    @PUT("user/update")
    fun update(
        @Header("Authorization") header: String?,
        @Body fields: RequestBody
    ): Call<ResponseBody?>?

    @GET("user/details")
    fun getUserDetail(@Query("id") id: String): Call<Profile?>?

    @POST("add/new/chat")
    fun addChat(@Body chat: Chat?): Call<Chat?>?

    @GET("user/profile")
    fun profileView(
        @Header("Authorization") header: String?,
    ): Call<Profile?>?

    @POST("user/send/verification/link?mailto=axil.ishan3@gmail.com")
    fun sendMail(): Call<ResponseBody?>?

    @FormUrlEncoded
    @GET("user/showfeed")
    fun showFeed(
        @Header("Authorization") header: String?,
        @Field("gender") gender: String?,
        @Field("slot") slot: String?
    ): Call<Feed?>?

    @POST("uploadfile")
    @Multipart
    fun getSlots()

    @FormUrlEncoded
    @POST("conversation")
    fun createConversation(@FieldMap fields: Map<String, String>): Call<ResponseBody?>?

    @GET("conversation")
    fun getAllConversations(@Header("Authorization") header: String?): Call<ArrayList<Conversation>?>?

    @GET("conversation/find/{userId}")
    fun getSpecificConversation(
        @Header("Authorization") header: String?,
        @Path("userId") userId: String
    ): Call<Conversation?>?

    @GET("message/{conversationId}")
    fun getAllMessages(
        @Header("Authorization") header: String?,
        @Path("conversationId") conversationId: String
    ): Call<ArrayList<Chat>?>?

    @POST("message")
    fun sendMessage(
        @Header("Authorization") header: String?,
        @Body fields: RequestBody
    ): Call<ResponseBody?>?

}

object Api {
    val retrofitService: ApiHolder by lazy {
        retrofit.create(ApiHolder::class.java)
    }

    val retrofitServiceForSlots: ApiHolder by lazy {
        retrofitForSlots.create(ApiHolder::class.java)
    }
}