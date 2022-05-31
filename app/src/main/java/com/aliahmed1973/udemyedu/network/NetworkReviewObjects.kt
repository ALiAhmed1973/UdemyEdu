package com.aliahmed1973.udemyedu.network

import com.aliahmed1973.udemyedu.database.DBReview
import com.aliahmed1973.udemyedu.model.Review
import com.aliahmed1973.udemyedu.model.ReviewUser
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class NetworkReviewContainer(@Json(name = "results") val reviews: List<NetworkReviews>, @Json(name = "count")val count:Int)

@JsonClass(generateAdapter = true)
data class NetworkReviewUser(
    @Json(name = "display_name")
    val name:String)

@JsonClass(generateAdapter = true)
data class NetworkReviews (
    @Json(name = "id")
    val id:Long,
    @Json(name = "content")
    val content:String,
    @Json(name = "rating")
    val rating:Float,
    @Json(name = "created")
    val createdTime:String,
    @Json(name = "user")
    val reviewUser: NetworkReviewUser
        )

fun NetworkReviewContainer.asReviewModel():List<Review>{
    return reviews.map {
        val reviewUser = ReviewUser( name = it.reviewUser.name)
        Review(id =it.id, content = it.content, rating = it.rating, createdTime = it.createdTime,reviewUser=reviewUser)
    }
}

fun NetworkReviewContainer.asDBreview(courseId:Int):List<DBReview>{
    return reviews.map {
        val reviewUser = ReviewUser( name = it.reviewUser.name)
        DBReview(id =it.id, content = it.content, rating = it.rating, createdTime = it.createdTime,reviewUser=reviewUser, courseid = courseId)
    }
}

