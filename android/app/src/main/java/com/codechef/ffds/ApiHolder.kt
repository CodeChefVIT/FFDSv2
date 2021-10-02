package com.codechef.ffds

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .readTimeout(180, TimeUnit.SECONDS)
    .connectTimeout(180, TimeUnit.SECONDS)
    .build()


val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl("https://ffds-backend.herokuapp.com/")
    .client(okHttpClient)
    .build()

val retrofitForSlots: Retrofit =
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://3.91.48.38")
        .client(okHttpClient)
        .build()

interface ApiHolder {
    @FormUrlEncoded
    @POST("user/register")
    fun register(@FieldMap fields: Map<String, String>): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("user/login")
    fun login(@FieldMap fields: Map<String, String>): Call<Token?>?

    @FormUrlEncoded
    @POST("user/email/verify")
    fun sendVerificationEmail(
        @Field("email") email: String?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("user/email/password")
    fun forgotPassword(
        @Field("email") email: String?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("user/reset")
    fun resetPassword(
        @Field("id") userId: String?,
        @Field("password") password: String?
    ): Call<ResponseBody?>?

    @PUT("user/update")
    fun update(
        @Header("Authorization") token: String?,
        @Body fields: RequestBody
    ): Call<ResponseBody?>?

    @GET("user/profile")
    fun profileView(
        @Header("Authorization") token: String?,
    ): Call<Profile?>?

    @GET("user/details")
    fun getUserDetail(
        @Query("id") id: String?
    ): Call<Profile?>?

    @POST("user/slot")
    @FormUrlEncoded
    fun slotMapper(
        @Field("Slots") slot: ArrayList<String>
    ): Call<Slots>?

    @GET("user/feed")
    fun getFeed(
        @Header("Authorization") token: String?,
    ): Call<Feed?>?

    @GET("user/reject/{userId}")
    fun rejectMatch(
        @Header("Authorization") token: String?,
        @Path("userId") userId: String?
    ): Call<ResponseBody>?

    @POST("user/image")
    @Multipart
    fun uploadImage(
        @Header("Authorization") token: String?,
        @Part image: MultipartBody.Part
    ): Call<Image?>?

    @FormUrlEncoded
    @POST("conversation")
    fun createNewConversation(
        @Header("Authorization") token: String?,
        @Field("userId") userId: String?
    ): Call<Conversation?>?

    @GET("conversation")
    fun getAllConversations(
        @Header("Authorization") token: String?
    ): Call<ConversationList?>?

    @GET("conversation/find/{userId}")
    fun getSpecificConversation(
        @Header("Authorization") token: String?,
        @Path("userId") userId: String?
    ): Call<Conversation?>?

    @GET("conversation/block/{userId}")
    fun blockUser(
        @Header("Authorization") token: String?,
        @Path("userId") userId: String?
    ): Call<ResponseBody>?

    @GET("conversation/unblock/{userId}")
    fun unBlockUser(
        @Header("Authorization") token: String?,
        @Path("userId") userId: String?
    ): Call<Conversation?>?

    @FormUrlEncoded
    @POST("message")
    fun sendMessage(
        @Header("Authorization") token: String?,
        @FieldMap fields: Map<String, Any>
    ): Call<ResponseBody?>?

    @GET("message/{conversationId}")
    fun getAllMessages(
        @Header("Authorization") token: String?,
        @Path("conversationId") conversationId: String
    ): Call<ArrayList<Chat>?>?

    @GET("message/last/{userId}")
    fun getLastMessage(
        @Path("userId") userId: String
    ): Call<Chat?>?

}

interface SlotsApiHolder {

    @POST("uploadfile")
    @Multipart
    fun getFreeSlots(
        @Part file: MultipartBody.Part
    ): Call<SlotMapper?>?
}

object Api {
    val retrofitService: ApiHolder by lazy {
        retrofit.create(ApiHolder::class.java)
    }

    val retrofitServiceForSlots: SlotsApiHolder by lazy {
        retrofitForSlots.create(SlotsApiHolder::class.java)
    }
}