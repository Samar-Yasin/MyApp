package com.example.myapp

import com.example.myapp.model.UpComingMoviesModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface APIsInterface {

    @GET("3/movie/upcoming?api_key=f713cdc02055b7ca35a5a99ce7999371")
    fun getUpcomingMoviesData(
        @Query("page") page: Int
    ): Call<UpComingMoviesModel>


    @GET("3/movie/popular?api_key=f713cdc02055b7ca35a5a99ce7999371")
    fun getPopularMoviesData(
        @Query("page") page: Int
    ): Call<UpComingMoviesModel>


    @GET("3/movie/top_rated?api_key=f713cdc02055b7ca35a5a99ce7999371")
    fun getTopRatedMoviesData(
        @Query("page") page: Int
    ): Call<UpComingMoviesModel>

    @Headers(
        "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmNzEzY2RjMDIwNTViN2NhMzVhNWE5OWNlNzk5OTM3MSIsInN1YiI6IjY0NDhjZmYyNmEyMjI3MDRhMGQxNWIxYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.38V847agAHztWoWKW1MsaGILxkWyP8RlyhmBpeJeT-s")
        @GET("3/search/movie")
        fun getSearchedMoviesData(
            @Query("query") query: String,
            @Query("include_adult") adult: Boolean,
            @Query("language") language: String,
            @Query("page") page: Int
        ): Call<UpComingMoviesModel>

                companion object {
            fun create(): APIsInterface {
                val interceptor = HttpLoggingInterceptor()
                interceptor.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor)

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/")
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                return retrofit.create(APIsInterface::class.java)
            }

        }

}