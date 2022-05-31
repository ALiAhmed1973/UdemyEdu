package com.aliahmed1973.udemyedu.network

import com.aliahmed1973.udemyedu.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL="https://www.udemy.com/api-2.0/"
private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit = Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL).build()

interface Service{
    @Headers("Accept: application/json, text/plain, */*",
        "Authorization: Basic "+BuildConfig.Authorization_Key,
        "Content-Type: application/json;charset=utf-8")
    @GET("courses")
   suspend fun getCourses(@Query("page") page:Int,@Query("page_size") pageSize:Int):NetworkCoursesContainer

    @Headers("Accept: application/json, text/plain, */*",
        "Authorization: Basic "+BuildConfig.Authorization_Key,
        "Content-Type: application/json;charset=utf-8")
    @GET("courses/{course_id}/reviews")
   suspend fun getReviews(@Path("course_id") course_id:Int,@Query("page") page: Int,@Query("page_size") pageSize:Int):NetworkReviewContainer
}

object CourseApi{
    val service :Service by lazy {
        retrofit.create(Service::class.java)
    }
}