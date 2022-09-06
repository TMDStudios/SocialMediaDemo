package com.example.socialmediademo.api

import com.example.socialmediademo.models.Post
import com.example.socialmediademo.models.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIInterface {
    @GET("users/")
    suspend fun getAllUsers(): Response<List<User>>

    @POST("users/")
    fun addUser(@Body userData: User): Call<User>

    @GET("login/{username}/{password}")
    suspend fun logIn(@Path("username") username: String, @Path("password") password: String): Response<String>

    @GET("users/{apiKey}")
    suspend fun getUser(@Path("apiKey") apiKey: String): Response<User>

    @GET("posts/")
    suspend fun getAllPosts(): Response<List<Post>>

    @GET("posts/{PostId}")
    suspend fun getPost(@Path("postId") postId: Int): Response<Post>

    @POST("posts/")
    fun addPost(@Body postData: Post): Call<Post>
}