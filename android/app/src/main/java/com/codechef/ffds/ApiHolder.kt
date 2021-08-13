package com.codechef.ffds

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl("https://ffds-backend.herokuapp.com/").build()

val loggingInterceptor: HttpLoggingInterceptor =
    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)

val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .readTimeout(180, TimeUnit.SECONDS)
    .connectTimeout(180, TimeUnit.SECONDS)
    .addInterceptor(loggingInterceptor)
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

    @PUT("user/update")
    fun update(
        @Header("Authorization") header: String?,
        @Body fields: RequestBody
    ): Call<ResponseBody?>?

    @GET("user/details")
    fun getUserDetail(@Query("id") id: String): Call<Profile?>?

    @GET("user/profile")
    fun profileView(
        @Header("Authorization") header: String?,
    ): Call<Profile?>?

    @POST("user/send/verification/link?mailto=axil.ishan3@gmail.com")
    fun sendMail(): Call<ResponseBody?>?

    @POST("user/slot")
    @FormUrlEncoded
    fun getSlots(
        @Field("Slots") slot: ArrayList<String>
    ): ArrayList<ArrayList<HashMap<String, Any>>>?

    @FormUrlEncoded
    @GET("user/showfeed")
    fun showFeed(
        @Header("Authorization") header: String?,
        @Field("gender") gender: String?,
        @Field("slot") slot: String?
    ): Call<Feed?>?

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

    @GET("message/last/{userId}")
    fun getLastMessage(
        @Path("userId") userId: String
    ): Call<Chat?>?

    @FormUrlEncoded
    @POST("message")
    fun sendMessage(
        @Header("Authorization") header: String?,
        @FieldMap fields: Map<String, Any>
    ): Call<ResponseBody?>?

}

interface SlotsApiHolder {

    @POST("uploadfile")
    @Multipart
    fun getFreeSlots(
        @Part file: MultipartBody.Part
    ): Call<ResponseBody?>?
}

object Api {
    val retrofitService: ApiHolder by lazy {
        retrofit.create(ApiHolder::class.java)
    }

    val retrofitServiceForSlots: SlotsApiHolder by lazy {
        retrofitForSlots.create(SlotsApiHolder::class.java)
    }
}